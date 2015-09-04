package com.hak.wymi.persistance.pojos.unsecure;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comment")
public class Comment implements HasPointsBalance {

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

    private Integer points;

    private Boolean deleted;

    private String content;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Comment> replies;

    @Version
    private Integer version;

    private Date updated;

    private Date created;

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
        this.content = content;
    }

    public Date getCreated() {
        if (this.created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return (Date) updated.clone();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getAuthorId() {
        return this.author.getUserId();
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
