package com.hak.wymi.persistance.pojos.comment;

import java.util.List;

public interface CommentCreationDao {
    boolean save(CommentCreation commentCreation);

    List<CommentCreation> getUnprocessed();
}
