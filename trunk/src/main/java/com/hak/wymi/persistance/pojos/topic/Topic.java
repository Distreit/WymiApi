package com.hak.wymi.persistance.pojos.topic;

import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.NameDoesNotExist;

import javax.persistence.*;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "topic")
@NameDoesNotExist(groups = {Topic.Creation.class})
public class Topic {
    public interface Creation {
    }

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
        return rentDueDate;
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
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
