package com.hak.wymi.persistance.pojos.comment;

public interface CommentTrialJurorDao {
    void save(CommentTrialJuror commentTrialJuror);

    CommentTrialJuror getExistingCurrent(String userName);

    void clearExpired();

    CommentTrialJuror get(Integer commentTrialJurorId);
}
