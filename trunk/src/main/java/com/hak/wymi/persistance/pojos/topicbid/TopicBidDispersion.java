package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.AbstractBalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.user.User;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TopicBidDispersion")
public class TopicBidDispersion extends AbstractBalanceTransaction {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TopicBidDispersion.class);
    private static final long serialVersionUID = 1510099288221998596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicBidDispersionId;

    @ManyToOne
    @JoinColumn(name = "destinationUserId")
    private User destinationUser;

    @ManyToOne
    @JoinColumn(name = "topicBidId")
    private TopicBid topicBid;

    private Integer amount;

    public TopicBidDispersion() {
        super();
    }

    public TopicBidDispersion(User destinationUser, TopicBid topicBid, int amount) {
        super();
        this.destinationUser = destinationUser;
        this.topicBid = topicBid;
        this.amount = amount;
        this.setState(TransactionState.UNPROCESSED);
    }

    @Override
    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public HasPointsBalance getSource() {
        return this.topicBid;
    }

    @Override
    public HasPointsBalance getDestination() {
        return this.destinationUser.getBalance();
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public String getTargetUrl() {
        return null;
    }

    @Override
    public Integer getTransactionId() {
        return this.topicBidDispersionId;
    }

    @Override
    public Object getDependent() {
        return null;
    }

    @Override
    public Integer getTaxerUserId() {
        return null;
    }

    @Override
    public Integer getTaxRate() {
        return 0;
    }

    @Override
    public boolean isUniqueToUser() {
        return false;
    }

    @Override
    public boolean paySiteTax() {
        return false;
    }

    @Override
    public Message getCancellationMessage() {
        LOGGER.error("Topic bid dispersion was cancelled for some reason. That shouldn't happen.");
        return null;
    }

    public void setDestinationUser(User destinationUser) {
        this.destinationUser = destinationUser;
    }

    public void setTopicBid(TopicBid topicBid) {
        this.topicBid = topicBid;
    }
}
