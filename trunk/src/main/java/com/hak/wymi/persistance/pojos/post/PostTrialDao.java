package com.hak.wymi.persistance.pojos.post;

public interface PostTrialDao {
    void save(PostTrial postTrial);

    void update(PostTrial postTrial);

    PostTrial get(Integer postId);
}
