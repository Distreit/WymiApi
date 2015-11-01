package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostTrialJurorDao {
    void save(PostTrialJuror postTrialJuror);

    List<PostTrialJuror> get(PostTrial postTrial);
}
