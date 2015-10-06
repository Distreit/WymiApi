package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.PostCreationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostCreationManager {
    @Autowired
    private PostCreationDao postCreationDao;

    @Transactional
    public boolean save(PostCreation transaction) {
        return postCreationDao.save(transaction);
    }

    @Transactional
    public List<PostCreation> getUnprocessed() {
        return postCreationDao.getUnprocessed();
    }
}
