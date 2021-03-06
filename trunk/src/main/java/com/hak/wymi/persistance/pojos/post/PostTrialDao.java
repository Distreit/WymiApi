package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.pojos.user.User;

import java.util.List;

public interface PostTrialDao {
    void save(PostTrial postTrial);

    void update(PostTrial postTrial);

    PostTrial get(Integer postId);

    List<PostTrial> getOnTrial();

    PostTrial getNextTrial(User user);
}
