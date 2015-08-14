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

    private Integer balance;

    @Version
    private Integer version;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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
        if (this.balance < amount) {
            throw new ValidationException("Not enough points.");
        }
        this.balance -= amount;
        return this.balance;
    }

    public Integer addPoints(Integer amount) {
        this.balance += amount;
        return this.balance;
    }
}
