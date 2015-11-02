package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.pojos.trial.Trial;
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
public class PostTrialDaoImpl implements PostTrialDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${jury.count.limit}")
    private Integer juryCountLimit;


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(PostTrial postTrial) {
        sessionFactory.getCurrentSession().save(postTrial);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(PostTrial postTrial) {
        sessionFactory.getCurrentSession().update(postTrial);
    }

    @Override
    public PostTrial get(Integer postId) {
        return (PostTrial) sessionFactory.getCurrentSession().get(PostTrial.class, postId);
    }

    @Override
    public List<PostTrial> getOnTrial() {
        return sessionFactory.getCurrentSession()
                .createCriteria(PostTrial.class)
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
    public Trial getNextTrial(User user) {
        return (Trial) sessionFactory
                .getCurrentSession()
                .createQuery("FROM PostTrial pt WHERE \n" +
                        "state = 'ON_TRIAL'\n" +
                        "AND postId NOT IN (\n" +
                        "    SELECT  ptj.postTrial.postId\n" +
                        "    FROM    PostTrialJuror as ptj \n" +
                        "    WHERE   ptj.user.userId=:userId ) \n" +
                        "AND (\n" +
                        "    SELECT  count(*) \n" +
                        "    FROM    PostTrialJuror ptj \n" +
                        "    WHERE   violatedSiteRuleVote is null \n" +
                        "    AND     isIllegalVote is null \n" +
                        "    AND     pt.post.postId = ptj.postTrial.postId ) <= :maxJurors \n" +
                        "ORDER BY created")
                .setParameter("userId", user.getUserId())
                .setParameter("maxJurors", new Long(juryCountLimit))
                .setMaxResults(1)
                .uniqueResult();
    }
}
