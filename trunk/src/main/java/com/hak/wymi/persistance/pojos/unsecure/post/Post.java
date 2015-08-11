package com.hak.wymi.persistance.pojos.unsecure.post;

import com.hak.wymi.persistance.pojos.unsecure.topic.Topic;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.validations.UrlOrText;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.ValidationException;
import java.util.Date;

@Entity
@Table(name = "post")
@UrlOrText(groups = Post.Creation.class)
public class Post {
    public interface Creation {
    }

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

    private Date updated;

    private Date created;

    public void addPoints(int amount) throws ValidationException {
        if (amount < 0) {
            throw new ValidationException("Not allowed to add negative points");
        }
        this.setPoints(this.getPoints() + amount);
    }

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
}
