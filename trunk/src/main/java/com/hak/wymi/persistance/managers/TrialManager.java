package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostTrial;
import com.hak.wymi.persistance.pojos.post.PostTrialDao;
import com.hak.wymi.persistance.pojos.post.PostTrialJuror;
import com.hak.wymi.persistance.pojos.post.PostTrialJurorDao;
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

    @Autowired
    private PostTrialJurorDao postTrialJurorDao;

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
    public PostTrialJuror get(String userName) {
        postTrialJurorDao.clearExpired();

        final PostTrialJuror juror = getExisting(userName);
        if (juror != null) {
            return juror;
        }

        return getNewJuror(userName);
    }

    private PostTrialJuror getExisting(String userName) {
        return postTrialJurorDao.getExistingCurrent(userName);
    }

    private PostTrialJuror getNewJuror(String userName) {
        final User user = userDao.getFromName(userName);
        final DateTime lastJurored = user.getLastJurored();
        if (lastJurored == null || lastJurored.plusSeconds(juryRateLimit).isBefore(DateTime.now())) {
            final PostTrial trial = postTrialDao.getNextTrial(user);
            if (trial != null) {
                final PostTrialJuror juror = new PostTrialJuror();
                juror.setPostTrial(trial);
                juror.setUser(user);
                postTrialJurorDao.save(juror);
                return juror;
            }
        }
        return null;
    }

}
