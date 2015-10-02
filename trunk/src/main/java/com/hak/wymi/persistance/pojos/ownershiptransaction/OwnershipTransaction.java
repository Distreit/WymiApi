package com.hak.wymi.persistance.pojos.ownershiptransaction;

import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ownershipTransaction")
public class OwnershipTransaction extends PersistentObject {
    private static final long serialVersionUID = -4652506078182812805L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownershipTransactionId;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "winningBidId")
    private TopicBid winningBid;

    @Enumerated(EnumType.STRING)
    private OwnershipTransactionState state;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime waitingPeriodExpiration;

    public OwnershipTransaction() {
        super();
    }

    public OwnershipTransaction(Topic topic, TopicBid winningBid) {
        super();
        this.topic = topic;
        this.winningBid = winningBid;
        this.state = OwnershipTransactionState.WAITING;
        this.waitingPeriodExpiration = new DateTime();
        this.waitingPeriodExpiration = this.waitingPeriodExpiration.plusDays(1).dayOfMonth().roundCeilingCopy();
    }

    public Integer getOwnershipTransactionId() {
        return ownershipTransactionId;
    }

    public void setOwnershipTransactionId(Integer ownershipTransactionId) {
        this.ownershipTransactionId = ownershipTransactionId;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public TopicBid getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(TopicBid winningBid) {
        this.winningBid = winningBid;
    }

    public OwnershipTransactionState getState() {
        return state;
    }

    public void setState(OwnershipTransactionState state) {
        this.state = state;
    }

    public DateTime getWaitingPeriodExpiration() {
        return waitingPeriodExpiration;
    }

    public void setWaitingPeriodExpiration(DateTime waitingPeriodExpiration) {
        this.waitingPeriodExpiration = waitingPeriodExpiration;
    }
}
