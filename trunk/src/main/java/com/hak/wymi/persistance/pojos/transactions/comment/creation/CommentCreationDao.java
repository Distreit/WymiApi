package com.hak.wymi.persistance.pojos.transactions.comment.creation;

import java.util.List;

public interface CommentCreationDao {
    boolean save(CommentCreation commentCreation);

    List<CommentCreation> getUnprocessed();
}
