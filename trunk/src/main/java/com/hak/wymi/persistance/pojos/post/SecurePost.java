package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.trial.TrialState;
import org.joda.time.DateTime;

public class SecurePost implements SecureToSend {
    private final Integer postId;
    private final String user;
    private final String topic;
    private final String title;
    private final Integer commentCount;
    private final String href;
    private final String text;
    private final Double score;
    private final Integer points;
    private final Boolean trashed;
    private final DateTime created;
    private final TrialState trialState;

    public SecurePost(Post post) {
        this.postId = post.getPostId();
        this.user = post.getUser().getName();
        this.topic = post.getTopic().getName();
        this.title = post.getTitle();
        this.commentCount = post.getCommentCounts();
        this.href = post.getHref();
        this.text = post.getText();
        this.score = post.getScore();
        this.points = post.getPoints();
        this.trashed = post.getTrashed();
        this.created = post.getCreated();
        if (post.getTrial() != null) {
            trialState = post.getTrial().getState();
        } else {
            trialState = null;
        }
    }

    public String getUser() {
        return user;
    }

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getText() {
        return text;
    }

    public Integer getPoints() {
        return points;
    }

    public DateTime getCreated() {
        return created;
    }

    public Integer getPostId() {
        return postId;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public Double getScore() {
        return score;
    }

    public Boolean getTrashed() {
        return trashed;
    }

    public TrialState getTrialState() {
        return trialState;
    }
}
