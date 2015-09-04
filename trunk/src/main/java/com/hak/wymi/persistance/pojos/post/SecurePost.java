package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.SecureToSend;

import java.util.Date;

public class SecurePost implements SecureToSend {
    private final Integer postId;
    private final String user;
    private final String topic;
    private final String title;
    private final String url;
    private final String text;
    private final Integer points;
    private final Date created;

    public SecurePost(Post post) {
        this.postId = post.getPostId();
        this.user = post.getUser().getName();
        this.topic = post.getTopic().getName();
        this.title = post.getTitle();
        this.url = post.getUrl();
        this.text = post.getText();
        this.points = post.getPoints();
        this.created = post.getCreated();
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

    public Date getCreated() {
        return (Date) created.clone();
    }

    public Integer getPostId() {
        return postId;
    }
}
