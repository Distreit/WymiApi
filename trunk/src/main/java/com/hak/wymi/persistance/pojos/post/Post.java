package com.hak.wymi.persistance.pojos.post;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.NegativePointsException;
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
public class Post extends PersistentObject implements HasPointsBalance {
    private static final long serialVersionUID = 8505649286670565143L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer postId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "post")
    private PostCreation postCreation;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "post")
    private PostTrial trial;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @NotNull
    private String title;

    private Boolean trashed = false;

    private Boolean deleted = false;

    @Formula("(select count(t.postId) from comment t where t.postId=postId)")
    private Integer commentCounts;

    private String href;

    private String text;

    private Boolean isText;

    private Integer points = 0;

    private Double score = 0.0;

    private Double base;

    private Integer donations = 0;

    private String deletedTitle;

    private String deletedHref;

    private String deletedText;

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

    public String getHref() {
        return href;
    }

    public void setHref(String url) {
        this.href = url;
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
    public void addPoints(Integer amount) throws InvalidValueException {
        if (this.deleted) {
            throw new UnsupportedOperationException("Cannot add points to a deleted post.");
        }

        if (amount < 0) {
            throw new NegativePointsException(amount, this);
        }
        this.setPoints(this.getPoints() + amount);
    }

    @Override
    public void removePoints(Integer amount) throws InvalidValueException {
        if (amount < 0) {
            throw new NegativePointsException(amount, this);
        }
        if (this.points < amount) {
            throw new InsufficientFundsException(amount, this);
        }
        this.setPoints(this.getPoints() - amount);
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

    public String getUrl() {
        return String.format("%s/%s", this.topic.getUrl(), this.postId);
    }

    public Boolean getTrashed() {
        return trashed;
    }

    public void setTrashed(Boolean trashed) {
        this.trashed = trashed;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonValue
    public SecurePost getSecurePost() {
        return new SecurePost(this);
    }

    public void delete() {
        this.deleted = true;
        this.deletedTitle = this.title;
        this.deletedHref = this.href;
        this.deletedText = this.text;

        this.title = "DELETED";
        this.href = "DELETED";
        this.text = "DELETED";
    }

    public String getDeletedTitle() {
        return deletedTitle;
    }

    public void setDeletedTitle(String deletedTitle) {
        this.deletedTitle = deletedTitle;
    }

    public String getDeletedHref() {
        return deletedHref;
    }

    public void setDeletedHref(String deletedHref) {
        this.deletedHref = deletedHref;
    }

    public String getDeletedText() {
        return deletedText;
    }

    public void setDeletedText(String deletedText) {
        this.deletedText = deletedText;
    }

    public PostTrial getTrial() {
        return trial;
    }

    public void setTrial(PostTrial trial) {
        this.trial = trial;
    }
}
