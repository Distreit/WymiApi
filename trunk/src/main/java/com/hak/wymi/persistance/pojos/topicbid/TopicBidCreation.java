package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionLog;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;

@Entity
@Table(name = "topicBidCreation")
public class TopicBidCreation extends PersistentObject implements BalanceTransaction {
    @Id
    private Integer topicBidId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @Null(groups = Creation.class)
    private TopicBid topicBid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionLogId")
    private TransactionLog transactionLog;

    @Min(value = 0)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Null(groups = Creation.class)
    private TransactionState state;

    public Integer getTopicBidId() {
        return topicBidId;
    }

    public void setTopicBidId(Integer topicBidId) {
        this.topicBidId = topicBidId;
    }

    public TopicBid getTopicBid() {
        return topicBid;
    }

    public void setTopicBid(TopicBid topicBid) {
        this.topicBid = topicBid;
    }

    @Override
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public Integer getSourceUserId() {
        return this.topicBid.getUser().getUserId();
    }

    @Override
    public HasPointsBalance getDestination() {
        return this.topicBid;
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public User getSourceUser() {
        return this.topicBid.getUser();
    }

    @Override
    public String getTargetUrl() {
        return this.topicBid.getTopic().getUrl();
    }

    @Override
    public Integer getTransactionId() {
        return this.topicBidId;
    }

    @Override
    public Object getDependent() {
        return this.topicBid;
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
    public TransactionState getState() {
        return state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public TransactionLog getTransactionLog() {
        return transactionLog;
    }

    @Override
    public void setTransactionLog(TransactionLog transactionLog) {
        this.transactionLog = transactionLog;
    }
}
