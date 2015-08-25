package com.hak.wymi.persistance.pojos.unsecure.post;

import java.util.List;

public interface PostDao {
    public boolean save(Post post);

    public List<Post> getAll(String topicName);

    public Post get(Integer id);
}
