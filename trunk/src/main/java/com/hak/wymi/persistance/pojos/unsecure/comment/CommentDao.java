package com.hak.wymi.persistance.pojos.unsecure.comment;

import java.security.Principal;
import java.util.List;

public interface CommentDao {

    List<Comment> getAll(Integer postId);

    Boolean save(Comment comment);

    Comment get(Integer commentId);

    boolean delete(Integer commentId, Principal principal);
}
