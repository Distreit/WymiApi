package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Post;

import java.util.List;

public interface PostDao {
    boolean save(Post post);

    List<Post> getAll(String topicName);

    Post get(Integer postId);
}
