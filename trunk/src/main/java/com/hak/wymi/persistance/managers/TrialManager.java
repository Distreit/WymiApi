package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.post.PostTrial;
import com.hak.wymi.persistance.pojos.post.PostTrialDao;
import com.hak.wymi.persistance.pojos.post.PostTrialJuror;
import com.hak.wymi.persistance.pojos.post.PostTrialJurorDao;
import com.hak.wymi.persistance.pojos.trial.TrialState;
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

    @Autowired
    private PostDao postDao;

    @Value("${jury.rate.limit}")
    private Integer juryRateLimit;

    @Value("${jury.count.limit}")
    private Integer jurorCountLimit;

    @Value("${trial.guilty.threshold}")
    private Integer trialGuiltyThreshold;


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

    /**
     * Updates user's juror timeout, updates juror results in database, and updates trial status, including handling
     * results of trial.
     *
     * @param transientJuror The user's ruling on a trial.
     * @param userName       The currently logged in user.
     */
    @Transactional
    public void update(PostTrialJuror transientJuror, String userName) {
        final PostTrialJuror juror = postTrialJurorDao.get(transientJuror.getPostTrialJurorId());

        if (juror.getUser().getName().equals(userName)) {
            juror.setIsIllegalVote(transientJuror.getIsIllegalVote());
            juror.setViolatedSiteRuleVote(transientJuror.getViolatedSiteRuleVote());

            final User user = juror.getUser();
            user.setLastJurored(new DateTime());
            userDao.update(user);

            updateTrialVotes(juror);
        } else {
            throw new UnsupportedOperationException("User not allowed to update juror results.");
        }
    }

    private void updateTrialVotes(PostTrialJuror juror) {
        final PostTrial trial = juror.getPostTrial();

        if (juror.getIsIllegalVote()) {
            trial.setIsIllegalVotes(trial.getIsIllegalVotes() + 1);
        }

        if (juror.getViolatedSiteRuleVote()) {
            trial.setViolatedSiteRuleVotes(trial.getViolatedSiteRuleVotes() + 1);
        }

        trial.setTotalVotes(trial.getTotalVotes() + 1);

        if (trial.getTotalVotes().equals(jurorCountLimit)) {
            final double guiltyVote = Math.max(trial.getViolatedSiteRuleVotes(), trial.getIsIllegalVotes()) / (double) jurorCountLimit;
            if (guiltyVote >= trialGuiltyThreshold / 100.0) {
                trial.setState(TrialState.GUILTY);
                trial.getPost().delete();
                postDao.update(trial.getPost());
            } else {
                trial.setState(TrialState.INNOCENT);
            }
        }
        postTrialDao.update(trial);
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
