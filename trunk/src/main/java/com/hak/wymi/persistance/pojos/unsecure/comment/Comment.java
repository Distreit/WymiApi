package com.hak.wymi.persistance.pojos.unsecure.comment;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.validations.Exists;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Entity
@Table(name = "comment")
public class Comment {

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

    private Date updated;

    private Date created;

    public interface Creation {
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
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
