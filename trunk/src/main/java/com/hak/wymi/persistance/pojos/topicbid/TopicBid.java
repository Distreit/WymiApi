package com.hak.wymi.persistance.pojos.topicbid;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Entity
@Table(name = "topicBid")
public class TopicBid implements HasPointsBalance {
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

    @NotNull
    @Min(value = 0)
    @Null(groups = {Creation.class})
    private Integer currentBalance;

    @Version
    @Null(groups = {Creation.class})
    private Integer version;

    @Null(groups = {Creation.class})
    private Date updated;

    @Null(groups = {Creation.class})
    private Date created;

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer amount) {
        this.currentBalance = amount;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
    public boolean addPoints(Integer amount) {
        if (amount >= 0) {
            this.currentBalance += amount;
            return true;
        }
        return false;
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
        // Doesn't need to track this.
    }

    @Override
    public String getName() {
        return this.topic.getName() + "Bid";
    }
}
