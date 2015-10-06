package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class CommentManager {
    @Autowired
    private CommentDao commentDao;

    @Transactional
    public Comment get(Integer commentId) {
        return commentDao.get(commentId);
    }

    @Transactional
    public List<Comment> getAll(Integer postId) {
        return commentDao.getAll(postId);
    }

    @Transactional
    public boolean delete(Integer commentId, Principal principal) {
        return commentDao.delete(commentId, principal);
    }
}
