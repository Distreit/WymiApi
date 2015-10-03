package com.hak.wymi.persistance.ranker;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRank;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRankDao;
import com.hak.wymi.utility.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ranks users based on donations. To use add a list of donations to the ranker then run the iterate method until the
 * delta (total change in rank) is less than a desired amount.
 */
public class UserTopicRanker implements SecureToSend {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTopicRanker.class);
    private static final int FAVORITE_LOOP_LENGTH = 5;

    private final ConcurrentMap<String, RankUser> users = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Double> ranks = new ConcurrentHashMap<>();

    private Double maxTotalOutLog10;

    public static void runOn(Topic topic,
                             CommentDonationDao commentDonationDao,
                             PostDonationDao postDonationDao,
                             UserTopicRankDao userTopicRankDao,
                             double minDelta,
                             int maxIterations,
                             double dampeningFactor,
                             UserTopicRanker userTopicRanker) {
        final List<? extends DonationTransaction> donations = Stream.concat(
                commentDonationDao.get(topic.getName()).stream(),
                postDonationDao.get(topic.getName()).stream()
        ).collect(Collectors.toList());

        userTopicRanker.addDonations(donations);
        Double delta = 1d;
        int iterationCount = 0;
        while (delta > minDelta && iterationCount < maxIterations) {
            delta = userTopicRanker.iterate(dampeningFactor);
            iterationCount += 1;
        }
        userTopicRankDao.save(userTopicRanker, topic);
    }

    /**
     * After creating a the UserTopicRanker add donations to it using this method.
     * <p/>
     * This method will create a user data structure based on incoming and outgoing donations, then prune the data
     * structure for favorite loops.
     *
     * @param donations A list of BalanceTransactions which are donations.
     */
    public void addDonations(List<? extends DonationTransaction> donations) {
        if (donations.size() > 0) {
            for (DonationTransaction donation : donations) {
                getRankUser(donation.getDestinationUser()).addIncomingDonation(donation);
                getRankUser(donation.getSourceUser()).addOutgoingDonation(donation);
            }

            pruneFavorites();

            maxTotalOutLog10 = Math.log10(findMaxTotalOut());
            createRanks();
        }
    }

    /**
     * After adding donations this method should be repeatedly run until the return value is less than a desired
     * amount.
     *
     * @param dampeningFactor The dampening factor should be between 0 and 1. Higher values are more stable and the
     *                        accepted default is around 0.85.
     *
     * @return Total combined change in the ranks of all users.
     */
    public double iterate(Double dampeningFactor) {
        final ConcurrentMap<String, Double> startingRanks = new ConcurrentHashMap<>(ranks);
        final int userCount = ranks.size();

        if (userCount > 0) {
            ranks.forEach((key, val) -> ranks.compute(key, (userName, rank) -> {
                rank = (1 - dampeningFactor) / userCount;
                rank += dampeningFactor * incomingLinkValues(userName, startingRanks, userCount);
                return rank;
            }));

            final double startingSum = startingRanks.values().stream().mapToDouble(Double::doubleValue).sum();
            final double endingSum = ranks.values().stream().mapToDouble(Double::doubleValue).sum();

            return Math.abs(startingSum - endingSum);
        }
        return 0d;
    }

    /**
     * Calculates the sum of all the individual user contributes to the receiving users rank. An individual shares a
     * portion of their rank based on how much they gave to the user compared to the total they gave to all users and
     * the maximum any user gave in total.
     *
     * @param receivingUserName The user receiving the donation.
     * @param ranks             The existing ranks of all users. This needs be a copy because of the iterative process.
     * @param userCount         The total number of users. Used when a user doesn't send any donations.
     *
     * @return The sum of all individual user contributions to the receiving user.
     */
    private double incomingLinkValues(String receivingUserName, ConcurrentMap<String, Double> ranks, int userCount) {
        double result = 0d;
        final RankUser receivingUser = users.get(receivingUserName);


        RankUser sendingUser;
        String sendingUserName;
        double userAddition;
        for (Entry<String, RankUser> userEntry : users.entrySet()) {
            userAddition = 0;
            sendingUser = userEntry.getValue();
            sendingUserName = userEntry.getKey();

            if (sendingUser.getTotalOut() > 0) {
                if (receivingUser.getIncomingDonations().containsKey(sendingUserName)) {
                    // User shares their rank when they donate to someone.
                    userAddition = ranks.get(sendingUserName);
                    // They share a portion of that rank based on how much they gave to the specific user vs the total they gave.
                    userAddition *= receivingUser.getIncomingDonations().get(sendingUserName) / (double) sendingUser.getTotalOut();
                    /**
                     * The result is then modified based on how much they gave total compared to the most anyone gave.
                     * This is so that users who give more are favored, but we don't want it to be too heavy so the
                     * amounts are log10.
                     */
                    userAddition *= Math.log10(sendingUser.getTotalOut()) / maxTotalOutLog10;
                }
            } else {
                // Users that don't donate to anyone spread their rank equally between all users.
                userAddition = ranks.get(sendingUserName) / userCount;
            }
            result += userAddition;
        }

        return result;
    }

    private void createRanks() {
        final Double defaultValue = 1.0 / users.size();

        users.keySet().stream().forEach(u -> ranks.put(u, defaultValue));
    }

    private Integer findMaxTotalOut() {
        final Integer max = users.values()
                .stream()
                .max(Comparator.comparing(RankUser::getTotalOut))
                .get()
                .getTotalOut();

        LOGGER.debug("Maximum donation: {}", max);
        return max;
    }

    private void pruneFavorites() {
        String loopUserName;
        for (RankUser user : users.values()) {
            loopUserName = favoriteLoop(user);
            if (loopUserName != null) {
                users.get(loopUserName).removeOutgoingDonation(user.getIncomingDonations().get(loopUserName));
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

    private RankUser getRankUser(User user) {

        if (!users.containsKey(user.getName())) {
            users.put(user.getName(), new RankUser(user));
        }
        return users.get(user.getName());
    }

    public ConcurrentMap<String, RankUser> getUsers() {
        return users;
    }

    public ConcurrentMap<String, Double> getRanks() {
        return ranks;
    }

    public List<UserTopicRank> getUserRanks(Topic topic) {
        return users.values()
                .stream()
                .map(u -> new UserTopicRank(u.getUser(), topic, ranks.get(u.getUserName())))
                .collect(Collectors.toList());
    }
}
