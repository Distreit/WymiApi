package com.hak.wymi.persistance.pojos.unsecure.post;

import java.util.List;

public interface PostDao {
    boolean save(Post post);

    List<Post> getAll(String topicName);

    Post get(Integer postId);
}
