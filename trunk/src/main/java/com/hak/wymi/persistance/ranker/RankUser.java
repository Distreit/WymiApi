package com.hak.wymi.persistance.ranker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.user.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RankUser {
    private final String userName;
    private final ConcurrentMap<String, Integer> incomingDonations = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> outgoingDonations = new ConcurrentHashMap<>();

    @JsonIgnore
    private final User user;
    private Integer inLink = 0;
    private Integer outLink = 0;
    private Integer totalIn = 0;
    private Integer totalOut = 0;
    private String favoriteName;
    private Integer favoriteValue = 0;

    public RankUser(User user) {
        this.userName = user.getName();
        this.user = user;
    }

    public void addIncomingDonation(BalanceTransaction donation) {
        final String donatorName = donation.getSource().getName();
        if (!incomingDonations.containsKey(donatorName)) {
            incomingDonations.put(donatorName, 0);
            inLink += 1;
        }

        incomingDonations.compute(donatorName, (k, v) -> v += donation.getAmount());
        totalIn += donation.getAmount();
    }

    public void removeIncomingDonation(String loopUserName) {
        if (incomingDonations.containsKey(loopUserName)) {
            totalIn -= incomingDonations.get(loopUserName);
            inLink -= 1;
            incomingDonations.remove(loopUserName);
        }
    }

    public void removeOutgoingDonation(Integer amount) {
        totalOut -= amount;
        outLink -= 1;
    }

    public void addOutgoingDonation(BalanceTransaction donation) {
        final String receiverName = donation.getDestination().getName();
        if (!outgoingDonations.containsKey(receiverName)) {
            outgoingDonations.put(receiverName, 0);
            outLink += 1;
        }

        outgoingDonations.compute(receiverName, (k, v) -> {
            v += donation.getAmount();
            if (v > favoriteValue) {
                favoriteValue = v;
                favoriteName = k;
            }
            return v;
        });
        totalOut += donation.getAmount();
    }

    public Integer getInLink() {
        return inLink;
    }

    public Integer getOutLink() {
        return outLink;
    }

    public Integer getTotalIn() {
        return totalIn;
    }

    public Integer getTotalOut() {
        return totalOut;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public Integer getFavoriteValue() {
        return favoriteValue;
    }

    public ConcurrentMap<String, Integer> getIncomingDonations() {
        return incomingDonations;
    }

    public String getUserName() {
        return userName;
    }

    public User getUser() {
        return user;
    }
}
