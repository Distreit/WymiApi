package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SecureComment {
    private Integer commentId;

    private String AuthorName;

    private Integer postId;

    private String content;

    private Date created;

    private List<SecureComment> replies;

    public SecureComment(Comment comment) {
        this.commentId = comment.getCommentId();
        this.AuthorName = comment.getAuthor().getName();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
        this.created = comment.getCreated();
        replies = new LinkedList<>();
        for (Comment reply : comment.getReplies()) {
            replies.add(new SecureComment(reply));
        }
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

    public List<SecureComment> getReplies() {
        return replies;
    }
}
