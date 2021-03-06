package com.hak.wymi.persistance.pojos.topic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.NameDoesNotExist;
import com.hak.wymi.validations.groups.Creation;
import com.hak.wymi.validations.groups.Update;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.Set;

@Entity
@Table(name = "topic")
@NameDoesNotExist(groups = {Creation.class})
public class Topic extends PersistentObject {
    private static final long serialVersionUID = -7800925884021933493L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class, Update.class})
    private Integer topicId;

    @Size(groups = {Default.class, Creation.class},
            min = 3,
            max = 30,
            message = "Name must be between 3 and 30 characters in length")
    @Pattern(groups = {Default.class, Creation.class},
            regexp = Constants.TOPIC_NAME_REGEX)
    @NotNull(groups = Update.class)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner")
    @Null(groups = {Creation.class, Update.class})
    private User owner;

    @NotNull(groups = {Default.class, Creation.class, Update.class})
    @Min(groups = {Default.class, Creation.class, Update.class}, value = 0)
    private Integer feeFlat;

    @NotNull(groups = {Default.class, Creation.class, Update.class})
    @Min(groups = {Default.class, Creation.class, Update.class}, value = 0)
    @Max(groups = {Default.class, Creation.class, Update.class}, value = 100)
    private Integer feePercent;

    @Null(groups = {Creation.class, Update.class})
    private Integer rent;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Null(groups = {Creation.class, Update.class})
    private DateTime rentDueDate;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "subscription",
            joinColumns = {@JoinColumn(name = "topicId")},
            inverseJoinColumns = {@JoinColumn(name = "userId")})
    @JsonIgnore
    private Set<User> subscribers;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "filters",
            joinColumns = {@JoinColumn(name = "topicId")},
            inverseJoinColumns = {@JoinColumn(name = "userId")})
    @JsonIgnore
    private Set<User> filters;

    @Null(groups = {Creation.class, Update.class})
    private Integer subscriberCount;

    @Null(groups = {Creation.class, Update.class})
    private Integer filterCount;

    @Size(groups = {Default.class, Creation.class},
            min = 1,
            max = 100,
            message = "title must be between 1 and 100 characters in length")
    @NotNull(groups = {Creation.class, Update.class})
    private String title;

    @Size(groups = {Default.class, Creation.class},
            min = 1,
            max = 500,
            message = "title must be between 1 and 500 characters in length")
    @NotNull(groups = {Creation.class, Update.class})
    private String description;

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

    public DateTime getRentDueDate() {
        return rentDueDate;
    }

    public void setRentDueDate(DateTime rentDueDate) {
        this.rentDueDate = rentDueDate;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public Integer getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(Integer filterCount) {
        this.filterCount = filterCount;
    }

    public Integer getFeeFlat() {
        return feeFlat;
    }

    public void setFeeFlat(Integer feeFlat) {
        this.feeFlat = feeFlat;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Integer feePercent) {
        this.feePercent = feePercent;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscriber) {
        this.subscribers = subscriber;
    }

    public boolean addSubscriber(User user) {
        if (subscribers.stream().noneMatch(u -> u.getUserId().equals(user.getUserId()))) {
            subscribers.add(user);
            subscriberCount = subscribers.size();
            return true;
        }
        return false;
    }

    public boolean removeSubscriber(User user) {
        if (subscribers.removeIf(u -> u.getUserId().equals(user.getUserId()))) {
            subscriberCount = subscribers.size();
            return true;
        }
        return false;
    }

    public boolean addFilter(User user) {
        if (filters.stream().noneMatch(u -> u.getUserId().equals(user.getUserId()))) {
            filters.add(user);
            filterCount = filters.size();
            return true;
        }
        return false;
    }

    public boolean removeFilter(User user) {
        if (filters.removeIf(u -> u.getUserId().equals(user.getUserId()))) {
            filterCount = filters.size();
            return true;
        }
        return false;
    }

    public Set<User> getFilters() {
        return filters;
    }

    public String getUrl() {
        return String.format("/t/%s", this.name);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonValue
    public SecureTopic getSecureTopic() {
        return new SecureTopic(this);
    }
}
