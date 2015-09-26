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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class UserTopicRanker implements SecureToSend {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTopicRanker.class);
    private static final int FAVORITE_LOOP_LENGTH = 5;

    private ConcurrentMap<String, RankUser> users = new ConcurrentHashMap<>();
    private Integer maxDonation = 0;

    public void addDonations(List<CommentDonation> commentDonations) {
        for (CommentDonation donation : commentDonations) {
            getRankUser(donation.getDestination().getName()).addIncomingDonation(donation);
            getRankUser(donation.getSourceUser().getName()).addOutgoingDonation(donation);
        }

        pruneFavorites();

        maxDonation = findMaxDonation();
    }

    private Integer findMaxDonation() {
        final List<Integer> values = users.values()
                .stream()
                .map(u -> u.getIncomingDonations().values())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        final Integer max = Collections.max(values);
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
            if (currentUser.getFavoriteName() == null || currentUser.getFavoriteName().equals("")) {
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
}
