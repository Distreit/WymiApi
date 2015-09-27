package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.AbstractPersistentObject;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.UrlOrText;
import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
@Table(name = "post")
@UrlOrText(groups = Creation.class)
public class Post extends AbstractPersistentObject implements HasPointsBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer postId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "post")
    private PostCreation postCreation;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @NotNull
    private String title;

    @Formula("(select count(t.postId) from comment t where t.postId=postId)")
    private Integer commentCounts;

    private String url;

    private String text;

    private Boolean isText;

    private Integer points;

    private Double score;

    private Double base;

    private Integer donations;

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
        this.score = base + points;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

    @Override
    public void incrementTransactionCount() {
        this.donations += 1;
    }

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public Integer getBalanceId() {
        return this.postId;
    }

    public Integer getTaxRate() {
        return this.postCreation.getFeePercent();
    }

    public Integer getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(Integer commentCounts) {
        this.commentCounts = commentCounts;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Integer getDonations() {
        return donations;
    }

    public void setDonations(Integer donations) {
        this.donations = donations;
    }
}
