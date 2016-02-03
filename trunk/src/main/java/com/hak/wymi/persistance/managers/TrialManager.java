package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.comment.CommentDao;
import com.hak.wymi.persistance.pojos.comment.CommentTrial;
import com.hak.wymi.persistance.pojos.comment.CommentTrialDao;
import com.hak.wymi.persistance.pojos.comment.CommentTrialJuror;
import com.hak.wymi.persistance.pojos.comment.CommentTrialJurorDao;
import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.post.PostTrial;
import com.hak.wymi.persistance.pojos.post.PostTrialDao;
import com.hak.wymi.persistance.pojos.post.PostTrialJuror;
import com.hak.wymi.persistance.pojos.post.PostTrialJurorDao;
import com.hak.wymi.persistance.pojos.trial.Juror;
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
    private CommentTrialDao commentTrialDao;

    @Autowired
    private PostTrialJurorDao postTrialJurorDao;

    @Autowired
    private CommentTrialJurorDao commentTrialJurorDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    @Value("${jury.rate.limit}")
    private Integer juryRateLimit;

    @Value("${jury.count.limit}")
    private Integer jurorCountLimit;

    @Value("${trial.guilty.threshold}")
    private Integer trialGuiltyThreshold;

    @Transactional
    public void createCommentTrial(Integer commentId, String userName) {
        final CommentTrial trial = new CommentTrial();

        trial.setCommentId(commentId);
        trial.setReporter(userDao.getFromName(userName));

        commentTrialDao.save(trial);
    }

    @Transactional
    public void createPostTrial(Integer postId, String userName) {
        final PostTrial trial = new PostTrial();

        trial.setPostId(postId);
        trial.setReporter(userDao.getFromName(userName));

        postTrialDao.save(trial);
    }

    @Transactional
    public Juror get(String userName) {
        postTrialJurorDao.clearExpired();
        commentTrialJurorDao.clearExpired();

        final Juror juror = getExisting(userName);
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

        if (juror.getUser().getName().equalsIgnoreCase(userName)) {
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

    /**
     * Updates user's juror timeout, updates juror results in database, and updates trial status, including handling
     * results of trial.
     *
     * @param transientJuror The user's ruling on a trial.
     * @param userName       The currently logged in user.
     */
    @Transactional
    public void update(CommentTrialJuror transientJuror, String userName) {
        final CommentTrialJuror juror = commentTrialJurorDao.get(transientJuror.getCommentTrialJurorId());

        if (juror.getUser().getName().equalsIgnoreCase(userName)) {
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

    private void updateTrialVotes(CommentTrialJuror juror) {
        final CommentTrial trial = juror.getCommentTrial();

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
                trial.getComment().delete();
                commentDao.update(trial.getComment());
            } else {
                trial.setState(TrialState.INNOCENT);
            }
        }
        commentTrialDao.update(trial);
    }


    private Juror getExisting(String userName) {
        Juror juror = postTrialJurorDao.getExistingCurrent(userName);

        if (juror == null) {
            juror = commentTrialJurorDao.getExistingCurrent(userName);
        }
        return juror;
    }

    private Juror getNewJuror(String userName) {
        final User user = userDao.getFromName(userName);
        final DateTime lastJurored = user.getLastJurored();

        if (lastJurored == null || lastJurored.plusSeconds(juryRateLimit).isBefore(DateTime.now())) {
            final PostTrial postTrial = postTrialDao.getNextTrial(user);
            final CommentTrial commentTrial = commentTrialDao.getNextTrial(user);

            if ((commentTrial == null && postTrial != null)
                    || (postTrial != null && postTrial.getCreated().isBefore(commentTrial.getCreated()))) {
                return getNewPostJuror(postTrial, user);

            } else if ((postTrial == null && commentTrial != null)
                    || (commentTrial != null && commentTrial.getCreated().isBefore(postTrial.getCreated()))) {
                return getNewCommentJuror(commentTrial, user);
            }
        }
        return null;
    }

    private Juror getNewPostJuror(PostTrial postTrial, User user) {
        if (postTrial != null) {
            final PostTrialJuror juror = new PostTrialJuror(postTrial, user);
            postTrialJurorDao.save(juror);
            return juror;
        }
        return null;
    }

    private Juror getNewCommentJuror(CommentTrial commentTrial, User user) {
        if (commentTrial != null) {
            final CommentTrialJuror juror = new CommentTrialJuror(commentTrial, user);
            commentTrialJurorDao.save(juror);
            return juror;
        }
        return null;
    }
}
