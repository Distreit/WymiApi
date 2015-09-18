package com.hak.wymi.persistance.pojos.topicBid;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "topicBidCreation")
public class TopicBidCreation implements BalanceTransaction {
    @Id
    private Integer topicBidId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @Null(groups = Creation.class)
    private TopicBid topicBid;

    @Min(value = 0)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Null(groups = Creation.class)
    private TransactionState state;

    @Version
    @Null(groups = Creation.class)
    private Integer version;

    @Null(groups = Creation.class)
    private Date updated;

    @Null(groups = Creation.class)
    private Date created;

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
    public Integer getDestinationId() {
        return this.topicBid.getTopicBidId();
    }

    @Override
    public Class getDestinationClass() {
        return this.topicBid.getClass();
    }

    @Override
    public HasPointsBalance getDestinationObject() {
        return this.topicBid;
    }

    @Override
    public Integer getTargetId() {
        return this.topicBid.getTopicBidId();
    }

    @Override
    public Class getTargetClass() {
        return this.topicBid.getClass();
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
    public TransactionState getState() {
        return state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
