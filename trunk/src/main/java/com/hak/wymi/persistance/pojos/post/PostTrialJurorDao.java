package com.hak.wymi.persistance.pojos.post;

public interface PostTrialJurorDao {
    void save(PostTrialJuror postTrialJuror);

    PostTrialJuror getExistingCurrent(String userName);

    void clearExpired();

    PostTrialJuror get(Integer postTrialJurorId);
}
