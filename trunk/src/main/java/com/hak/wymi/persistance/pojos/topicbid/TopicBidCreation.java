package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.AbstractBalanceTransaction;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;

@Entity
@Table(name = "topicBidCreation")
public class TopicBidCreation extends AbstractBalanceTransaction {
    private static final long serialVersionUID = -6621645087111568268L;

    @Id
    private Integer topicBidId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @Null(groups = Creation.class)
    private TopicBid topicBid;

    @Min(value = 0)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer amount;

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
    public HasPointsBalance getDestination() {
        return this.topicBid;
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public HasPointsBalance getSource() {
        return this.topicBid.getUser().getBalance();
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
}
