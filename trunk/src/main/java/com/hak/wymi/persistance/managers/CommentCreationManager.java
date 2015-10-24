package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.CommentCreationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentCreationManager {
    @Autowired
    private CommentCreationDao commentCreationDao;

    @Transactional
    public List<CommentCreation> getUnprocessed() {
        return commentCreationDao.getUnprocessed();
    }
}
