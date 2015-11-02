package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostTrial;
import com.hak.wymi.persistance.pojos.post.PostTrialDao;
import com.hak.wymi.persistance.pojos.trial.Trial;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrialManager {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostTrialDao postTrialDao;

    @Value("${jury.rate.limit}")
    private Integer juryRateLimit;

    @Value("${jury.count.limit}")
    private Integer jurorCountLimit;


    @Transactional
    public void create(Integer postId, String userName) {
        final PostTrial trial = new PostTrial();

        trial.setPostId(postId);
        trial.setReporter(userDao.getFromName(userName));

        postTrialDao.save(trial);
    }

    @Transactional
    public Trial get(String userName) {

        final User user = userDao.getFromName(userName);
        final DateTime lastJurored = user.getLastJurored();
        if (lastJurored == null || lastJurored.plusSeconds(juryRateLimit).isBefore(DateTime.now())) {
            final Trial trial = postTrialDao.getNextTrial(user);
            return trial;
        }
        return null;
    }

}
