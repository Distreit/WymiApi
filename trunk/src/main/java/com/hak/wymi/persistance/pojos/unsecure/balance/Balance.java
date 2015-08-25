package com.hak.wymi.persistance.pojos.unsecure.balance;

import com.hak.wymi.persistance.pojos.unsecure.user.User;

import javax.persistence.*;
import javax.xml.bind.ValidationException;

@Entity
@Table(name = "balance")
public class Balance {
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

    public Integer removePoints(Integer amount) throws ValidationException {
        if (this.currentBalance < amount) {
            throw new ValidationException("Not enough points.");
        }
        this.currentBalance -= amount;
        return this.currentBalance;
    }

    public Integer addPoints(Integer amount) {
        this.currentBalance += amount;
        return this.currentBalance;
    }
}
