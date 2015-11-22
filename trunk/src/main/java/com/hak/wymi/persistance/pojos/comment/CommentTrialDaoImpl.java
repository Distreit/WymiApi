package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.pojos.trial.TrialState;
import com.hak.wymi.persistance.pojos.user.User;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentTrialDaoImpl implements CommentTrialDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${jury.count.limit}")
    private Long juryCountLimit;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(CommentTrial commentTrial) {
        sessionFactory.getCurrentSession().save(commentTrial);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(CommentTrial commentTrial) {
        sessionFactory.getCurrentSession().update(commentTrial);
    }

    @Override
    public CommentTrial get(Integer commentId) {
        return (CommentTrial) sessionFactory.getCurrentSession().get(CommentTrial.class, commentId);
    }

    @Override
    public List<CommentTrial> getOnTrial() {
        return sessionFactory.getCurrentSession()
                .createCriteria(CommentTrial.class)
                .add(Restrictions.eq("state", TrialState.ON_TRIAL))
                .addOrder(Order.asc("created"))
                .list();
    }

    /**
     * @param user The current user
     *
     * @return the next trial a user should vote on. Meaning there are less than ${jury.count.limit} jurors, the user
     * isn't already a juror on the trial, and the trail hasn't already finished.
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CommentTrial getNextTrial(User user) {
        return (CommentTrial) sessionFactory
                .getCurrentSession()
                .createQuery("FROM CommentTrial ct WHERE \n" +
                        "state = 'ON_TRIAL'\n" +
                        "AND ct.comment.commentId NOT IN (\n" +
                        "    SELECT  ctj.commentTrial.commentId\n" +
                        "    FROM    CommentTrialJuror as ctj \n" +
                        "    WHERE   ctj.user.userId=:userId ) \n" +
                        "AND (\n" +
                        "    SELECT  count(*) \n" +
                        "    FROM    CommentTrialJuror ctj \n" +
                        "    WHERE   violatedSiteRuleVote is null \n" +
                        "    AND     isIllegalVote is null \n" +
                        "    AND     ct.comment.commentId = ctj.commentTrial.commentId ) <= :maxJurors \n" +
                        "ORDER BY created")
                .setParameter("userId", user.getUserId())
                .setParameter("maxJurors", juryCountLimit)
                .setMaxResults(1)
                .uniqueResult();
    }
}
