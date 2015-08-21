package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SecureComment {
    private Integer commentId;

    private String authorName;

    private Integer postId;

    private String content;

    private Date created;

    private List<SecureComment> replies;

    public SecureComment(Comment comment) {
        if (!comment.getDeleted()) {
            this.authorName = comment.getAuthor().getName();
            this.content = comment.getContent();
        } else {
            this.authorName = "[DELETED]";
            this.content = "[DELETED]";
        }

        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.created = comment.getCreated();
        replies = new LinkedList<>();

        replies.addAll(comment.getReplies().stream().map(SecureComment::new).collect(Collectors.toList()));
    }

    public Integer getCommentId() {
        return commentId;
    }

    public String getAuthorName() {
        return authorName;
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
