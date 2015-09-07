package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostCreationDao {
    boolean save(PostCreation postCreation);

    List<PostCreation> getUnprocessed();
}
