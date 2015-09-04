package com.hak.wymi.persistance.pojos.balance;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "balance")
public class Balance implements HasPointsBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String balanceId;

    @OneToOne
    @JoinColumn(name = "userId")
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

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
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
    public boolean addPoints(Integer amount) {
        if (amount >= 0) {
            this.currentBalance += amount;
            return true;
        }
        return false;
    }
}
