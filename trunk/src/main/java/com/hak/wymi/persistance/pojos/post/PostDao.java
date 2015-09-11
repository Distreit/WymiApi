package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostDao {
    List<Post> get(String topicName, int firstResult, int maxResults);

    Post get(Integer postId);
}
