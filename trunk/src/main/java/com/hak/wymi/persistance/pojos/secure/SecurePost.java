package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;

import java.util.Date;

public class SecurePost {
    private final Integer id;
    private final String user;
    private final String topic;
    private final String title;
    private final String url;
    private final String text;
    private final Integer points;
    private final Date created;

    public SecurePost(Post post) {
        this.id = post.getPostId();
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

    public Integer getId() {
        return id;
    }
}
