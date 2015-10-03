package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import org.joda.time.DateTime;

public class SecurePost implements SecureToSend {
    private final Integer postId;
    private final String user;
    private final String topic;
    private final String title;
    private final Integer commentCount;
    private final String url;
    private final String text;
    private final Double score;
    private final Integer points;
    private final DateTime created;

    public SecurePost(Post post) {
        this.postId = post.getPostId();
        this.user = post.getUser().getName();
        this.topic = post.getTopic().getName();
        this.title = post.getTitle();
        this.commentCount = post.getCommentCounts();
        this.url = post.getHref();
        this.text = post.getText();
        this.points = post.getPoints();
        this.created = post.getCreated();
        this.score = post.getScore();
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

    public String getUrl() {
        return url;
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
}
