package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;

import java.util.Date;

public class SecureComment {
    private Integer commentId;

    private String AuthorName;

    private Integer postId;

    private String content;

    private Date created;

    public SecureComment(Comment comment) {
        this.commentId = comment.getCommentId();
        this.AuthorName = comment.getAuthor().getName();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
        this.created = comment.getCreated();
    }

    public Integer getCommentId() {
        return commentId;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public Integer getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreated() {
        return created;
    }
}
