package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.trial.TrialState;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SecureComment implements SecureToSend {
    private final Integer commentId;

    private final String authorName;

    private final Integer postId;

    private final String content;

    private final DateTime created;

    private final Boolean deleted;

    private final Boolean trashed;

    private final List<SecureComment> replies;

    private final Integer replyCount;

    private final TrialState trialState;

    private final Integer points;

    private final Integer depth;

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
        this.points = comment.getPoints();
        this.replies = new LinkedList<>();
        this.replyCount = comment.getReplyCount();
        this.deleted = comment.getDeleted();
        this.trashed = comment.getTrashed();
        this.depth = comment.getDepth();

        if (comment.getReplies() != null) {
            this.replies.addAll(comment.getReplies().stream().map(SecureComment::new).collect(Collectors.toList()));
        }

        if (comment.getTrial() != null) {
            trialState = comment.getTrial().getState();
        } else {
            trialState = null;
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

    public DateTime getCreated() {
        return created;
    }

    public List<SecureComment> getReplies() {
        return replies;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Boolean getTrashed() {
        return trashed;
    }

    public TrialState getTrialState() {
        return trialState;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getDepth() {
        return depth;
    }
}
