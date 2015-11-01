package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostTrial;
import com.hak.wymi.persistance.pojos.post.PostTrialDao;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrialManager {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostTrialDao postTrialDao;

    @Transactional
    public void create(Integer postId, String userName) {
        final PostTrial trial = new PostTrial();

        trial.setPostId(postId);
        trial.setReporter(userDao.getFromName(userName));

        postTrialDao.save(trial);
    }
}
