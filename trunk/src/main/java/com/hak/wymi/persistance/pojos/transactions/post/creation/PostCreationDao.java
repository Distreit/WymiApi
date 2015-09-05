package com.hak.wymi.persistance.pojos.transactions.post.creation;

import java.util.List;

public interface PostCreationDao {
    boolean save(PostCreation postCreation);

    List<PostCreation> getUnprocessed();
}
