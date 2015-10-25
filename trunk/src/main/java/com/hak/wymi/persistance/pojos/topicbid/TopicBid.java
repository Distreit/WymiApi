package com.hak.wymi.persistance.pojos.topicbid;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.NegativePointsException;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
@Table(name = "topicBid")
public class TopicBid extends PersistentObject implements HasPointsBalance {
    private static final long serialVersionUID = 1956500952252914355L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class})
    private Integer topicBidId;

    @ManyToOne
    @JoinColumn(name = "userId")
    @Null(groups = {Creation.class})
    private User user;

    @ManyToOne
    @JoinColumn(name = "topicId")
    @Null(groups = {Creation.class})
    private Topic topic;

    @OneToOne(mappedBy = "topicBid", fetch = FetchType.LAZY)
    private TopicBidCreation topicBidCreation;

    @NotNull
    @Min(value = 0)
    @Null(groups = {Creation.class})
    private Integer currentBalance = 0;

    @Enumerated(EnumType.STRING)
    private TopicBidState state = TopicBidState.WAITING;

    protected TopicBid() {
        super();
    }

    public TopicBid(User user, Topic topic) {
        super();
        this.user = user;
        this.topic = topic;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer amount) {
        this.currentBalance = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Integer getTopicBidId() {
        return topicBidId;
    }

    public void setTopicBidId(Integer topicBidId) {
        this.topicBidId = topicBidId;
    }

    @Override
    public void addPoints(Integer amount) throws InvalidValueException {
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

    @Override
    public void incrementTransactionCount() {
        // Doesn't need to track this.
    }

    @Override
    public String getName() {
        return this.topic.getName() + "Bid";
    }

    @Override
    public Integer getBalanceId() {
        return topicBidId;
    }

    public TopicBidState getState() {
        return state;
    }

    public void setState(TopicBidState state) {
        this.state = state;
    }

    public TopicBidCreation getTopicBidCreation() {
        return topicBidCreation;
    }

    public void setTopicBidCreation(TopicBidCreation topicBidCreation) {
        this.topicBidCreation = topicBidCreation;
    }

    @JsonValue
    public SecureTopicBid getSecureTopicBid() {
        return new SecureTopicBid(this);
    }
}
