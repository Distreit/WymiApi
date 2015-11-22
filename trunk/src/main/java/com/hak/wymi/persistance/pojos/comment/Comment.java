package com.hak.wymi.persistance.pojos.comment;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.NegativePointsException;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "comment")
public class Comment extends PersistentObject implements HasPointsBalance {
    private static final long serialVersionUID = 1949625540493733465L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class})
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "authorId")
    @Null(groups = {Creation.class})
    private User author;

    @ManyToOne
    @JoinColumn(name = "postId")
    @Null(groups = {Creation.class})
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parentCommentId")
    private Comment parentComment;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "comment")
    @Null(groups = {Creation.class})
    private CommentCreation commentCreation;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "comment")
    private CommentTrial trial;

    private Integer points;

    private Integer donations;

    private Double score;

    private Double base;

    private Boolean deleted;

    private Boolean trashed;

    @Size(max = 10000, min = 1, groups = {Creation.class})
    private String content;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 10)
    private List<Comment> replies;

    @Formula("(select count(c.commentId) from comment c where c.parentCommentId=commentId)")
    private Integer replyCount;

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
        this.score = base + points;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (this.deleted != null && this.deleted) {
            throw new UnsupportedOperationException("Cannot update deleted comment.");
        }
        this.content = content;
    }

    public Integer getAuthorId() {
        return this.author.getUserId();
    }

    @Override
    public void addPoints(Integer amount) throws InvalidValueException {
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
        return String.format("Post: %s - Comment: %s", this.post.getName(), this.commentId);
    }

    @Override
    public Integer getBalanceId() {
        return this.commentId;
    }

    public Integer getTaxRate() {
        return commentCreation.getFeePercent();
    }

    public CommentCreation getCommentCreation() {
        return commentCreation;
    }

    public void setCommentCreation(CommentCreation commentCreation) {
        this.commentCreation = commentCreation;
    }

    public Integer getDonations() {
        return donations;
    }

    public void setDonations(Integer donations) {
        this.donations = donations;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getUrl() {
        return String.format("%s/comments/%s", this.getPost().getUrl(), this.commentId);
    }

    @JsonValue
    public SecureComment getSecureComment() {
        return new SecureComment(this);
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Boolean getTrashed() {
        return trashed;
    }

    public void setTrashed(Boolean trashed) {
        this.trashed = trashed;
    }

    public CommentTrial getTrial() {
        return trial;
    }

    public void setTrial(CommentTrial trial) {
        this.trial = trial;
    }

    public void delete() {
        this.deleted = true;
        this.content = "";
    }
}
