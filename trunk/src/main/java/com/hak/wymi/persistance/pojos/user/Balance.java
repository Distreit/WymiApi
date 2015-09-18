package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "balance")
public class Balance implements HasPointsBalance {
    @Id
    private Integer userId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    private Integer currentBalance;

    @Version
    private Integer version;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
    public boolean addPoints(Integer amount) {
        if (amount >= 0) {
            this.currentBalance += amount;
            return true;
        }
        return false;
    }
}
