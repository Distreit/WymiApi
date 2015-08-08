package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;

import java.util.Date;

public class SecurePost {
    private String user;
    private String topic;
    private String title;
    private String url;
    private String text;
    private Integer points;
    private Date created;

    public SecurePost(Post post) {
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
