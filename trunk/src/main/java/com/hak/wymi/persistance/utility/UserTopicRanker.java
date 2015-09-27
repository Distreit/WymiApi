package com.hak.wymi.persistance.utility;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.utility.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class UserTopicRanker implements SecureToSend {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTopicRanker.class);
    private static final int FAVORITE_LOOP_LENGTH = 5;

    private final ConcurrentMap<String, RankUser> users = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Double> ranks = new ConcurrentHashMap<>();

    private Double maxDonationLog10;

    public Double iterate(Double dampeningFactor) {
        final ConcurrentMap<String, Double> startingRanks = new ConcurrentHashMap<>(ranks);
        final int userCount = ranks.size();

        ranks.forEach((key, val) -> ranks.compute(key, (userName, rank) -> {
            rank = (1 - dampeningFactor) / userCount;
            rank += dampeningFactor * incomingLinkValues(userName, startingRanks, userCount);
            return rank;
        }));

        Double startingSum = startingRanks.values().stream().mapToDouble(Double::doubleValue).sum();
        Double endingSum = ranks.values().stream().mapToDouble(Double::doubleValue).sum();

        return Math.abs(startingSum - endingSum);
    }

    private double incomingLinkValues(String userName, ConcurrentMap<String, Double> ranks, int userCount) {
        double result = 0d;
        RankUser receivingUser = users.get(userName);

        for (Entry<String, RankUser> userEntry : users.entrySet()) {
            double userAddition = 0;
            RankUser sendingUser = userEntry.getValue();
            String sendingUserName = userEntry.getKey();

            if (sendingUser.getTotalOut() > 0) {
                if (receivingUser.getIncomingDonations().containsKey(sendingUserName)) {
                    Integer incomingDonationAmount = receivingUser.getIncomingDonations().get(sendingUserName);

                    userAddition = ranks.get(sendingUserName);
                    userAddition *= incomingDonationAmount / (double) sendingUser.getTotalOut();
                    userAddition *= Math.log10(incomingDonationAmount) / maxDonationLog10;
                }
            } else {
                userAddition = ranks.get(sendingUserName) / userCount;
            }
            result += userAddition;
        }

        return result;
    }

    public void addDonations(List<CommentDonation> commentDonations) {
        for (CommentDonation donation : commentDonations) {
            getRankUser(donation.getDestination().getName()).addIncomingDonation(donation);
            getRankUser(donation.getSourceUser().getName()).addOutgoingDonation(donation);
        }

        pruneFavorites();

        maxDonationLog10 = Math.log10(findMaxDonation());
        createRanks();
    }

    private void createRanks() {
        final Double defaultValue = 1.0 / users.size();

        users.keySet().stream().forEach(u -> ranks.put(u, defaultValue));
    }

    private Integer findMaxDonation() {
        final List<Integer> values = users.values()
                .stream()
                .map(u -> u.getIncomingDonations().values())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        final Integer max = Collections.max(values);
        LOGGER.debug("Maximum donation: {}", max);
        return max;
    }

    private void pruneFavorites() {
        String loopUserName;
        for (RankUser user : users.values()) {
            loopUserName = favoriteLoop(user);
            if (loopUserName != null) {
                user.removeIncomingDonation(loopUserName);
            }
        }
    }

    private String favoriteLoop(RankUser user) {
        final String startingUserName = user.getUserName();
        final List<String> path = new LinkedList<>();
        path.add(startingUserName);
        RankUser currentUser = user;


        for (int i = 0; i < FAVORITE_LOOP_LENGTH; i += 1) {
            if (currentUser.getFavoriteName() == null || "".equals(currentUser.getFavoriteName())) {
                return null;
            }

            if (currentUser.getFavoriteName().equals(startingUserName)) {
                LOGGER.debug("Found loop in user favorites: {}", JSONConverter.getJSON(path, Boolean.FALSE));
                return path.get(path.size() - 1);
            }

            path.add(currentUser.getFavoriteName());
            currentUser = users.get(currentUser.getFavoriteName());
        }
        return null;
    }

    private RankUser getRankUser(String userName) {
        if (!users.containsKey(userName)) {
            users.put(userName, new RankUser(userName));
        }
        return users.get(userName);
    }

    public ConcurrentMap<String, RankUser> getUsers() {
        return users;
    }

    public ConcurrentMap<String, Double> getRanks() {
        return ranks;
    }
}
