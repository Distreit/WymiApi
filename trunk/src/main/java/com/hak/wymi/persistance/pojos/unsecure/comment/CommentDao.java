package com.hak.wymi.persistance.pojos.unsecure.comment;

import java.security.Principal;
import java.util.List;

public interface CommentDao {

    public List<Comment> getAll(Integer postId);

    public Boolean save(Comment comment);

    public Comment get(Integer commentId);

    public boolean delete(Integer commentId, Principal principal);
}
