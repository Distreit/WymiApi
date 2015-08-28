package com.hak.wymi.persistance.pojos.unsecure;

import com.hak.wymi.validations.NameDoesNotExist;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "topic")
@NameDoesNotExist(groups = {Creation.class})
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer topicId;

    @Size(
            groups = {Default.class, Creation.class},
            min = 3,
            max = 30,
            message = "Name must be between 3 and 30 characters in length"
    )
    @Pattern(
            groups = {Default.class, Creation.class},
            regexp = "[0-9a-zA-Z][0-9a-zA-Z-]+"
    )
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner")
    @Null(groups = Creation.class)
    private User owner;

    @Null(groups = Creation.class)
    private Integer rent;

    @Null(groups = Creation.class)
    private Date rentDueDate;

    @Null(groups = Creation.class)
    private Integer subscribers;

    @Null(groups = Creation.class)
    private Integer unsubscribers;

    @Null(groups = Creation.class)
    private Date created;

    @Version
    private Integer version;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getRent() {
        return rent;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public Date getRentDueDate() {
        return (Date) rentDueDate.clone();
    }

    public void setRentDueDate(Date rentDueDate) {
        this.rentDueDate = rentDueDate;
    }

    public Integer getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Integer subscribers) {
        this.subscribers = subscribers;
    }

    public Integer getUnsubscribers() {
        return unsubscribers;
    }

    public void setUnsubscribers(Integer unsubscribers) {
        this.unsubscribers = unsubscribers;
    }

    public Date getCreated() {
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}