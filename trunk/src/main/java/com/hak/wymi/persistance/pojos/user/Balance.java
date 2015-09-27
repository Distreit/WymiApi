package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.AbstractPersistentObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "balance")
public class Balance extends AbstractPersistentObject implements HasPointsBalance {
    @Id
    private Integer userId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    private Integer currentBalance;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer balance) {
        this.currentBalance = balance;
    }

    @Override
    public boolean removePoints(Integer amount) {
        if (amount >= 0 && this.currentBalance >= amount) {
            this.currentBalance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public void incrementTransactionCount() {
        // Doesn't need to track transaction count.
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public Integer getBalanceId() {
        return this.userId;
    }

    @Override
    public boolean addPoints(Integer amount) {
        if (amount >= 0) {
            this.currentBalance += amount;
            return true;
        }
        return false;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
