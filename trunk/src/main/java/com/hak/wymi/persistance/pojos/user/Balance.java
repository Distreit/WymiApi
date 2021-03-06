package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.NegativePointsException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "balance")
public class Balance extends PersistentObject implements HasPointsBalance {
    private static final long serialVersionUID = -5657474662863598069L;

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
    public void addPoints(Integer amount) throws InvalidValueException {
        if (!user.getValidated()) {
            throw new UnsupportedOperationException("Cannot add points to a unvalidated user.");
        }

        if (amount < 0) {
            throw new NegativePointsException(amount, this);
        }
        this.currentBalance += amount;
    }

    @Override
    public void removePoints(Integer amount) throws InvalidValueException {
        if (amount < 0) {
            throw new NegativePointsException(amount, this);
        }
        if (this.currentBalance < amount) {
            throw new InsufficientFundsException(amount, this);
        }
        this.currentBalance -= amount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
