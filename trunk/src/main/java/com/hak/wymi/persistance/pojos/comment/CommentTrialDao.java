package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.pojos.user.User;

import java.util.List;

public interface CommentTrialDao {
    void save(CommentTrial commentTrial);

    void update(CommentTrial commentTrial);

    CommentTrial get(Integer commentId);

    List<CommentTrial> getOnTrial();

    CommentTrial getNextTrial(User user);
}
