package com.hak.wymi.persistance.pojos.unsecure;

import com.hak.wymi.persistance.pojos.unsecure.interfaces.HasPointsBalance;
import com.hak.wymi.validations.UrlOrText;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Entity
@Table(name = "post")
@UrlOrText(groups = Creation.class)
public class Post implements HasPointsBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @NotNull
    private String title;

    private String url;

    private String text;

    private Boolean isText;

    private Integer points;

    private Double score;

    @Version
    private Integer version;

    private Date updated;

    private Date created;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsText() {
        return isText;
    }

    public void setIsText(Boolean isText) {
        this.isText = isText;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getUpdated() {
        return (Date) updated.clone();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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

    @Override
    public boolean addPoints(Integer amount) {
        if (amount >= 0) {
            this.setPoints(this.getPoints() + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePoints(Integer amount) {
        if (amount >= 0 && this.getPoints() >= 0) {
            this.setPoints(this.getPoints() - amount);
            return true;
        }
        return false;
    }
}
