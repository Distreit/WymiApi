package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostDao {
    List<Post> getAll(String topicName);

    Post get(Integer postId);
}
