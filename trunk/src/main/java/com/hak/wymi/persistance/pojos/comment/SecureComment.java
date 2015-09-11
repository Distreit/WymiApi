package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.interfaces.SecureToSend;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SecureComment implements SecureToSend {
    private final Integer commentId;

    private final String authorName;

    private final Integer postId;

    private final String content;

    private final Date created;

    private final Double score;

    private final List<SecureComment> replies;

    public SecureComment(Comment comment) {
        if (comment.getDeleted()) {
            this.authorName = "[DELETED]";
            this.content = "[DELETED]";
        } else {
            this.authorName = comment.getAuthor().getName();
            this.content = comment.getContent();
        }

        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.created = comment.getCreated();
        this.score = comment.getScore();
        replies = new LinkedList<>();

        if (comment.getReplies() != null) {
            replies.addAll(comment.getReplies().stream().map(SecureComment::new).collect(Collectors.toList()));
        }
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
        if (this.created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    public List<SecureComment> getReplies() {
        return replies;
    }

    public Double getScore() {
        return score;
    }
}
