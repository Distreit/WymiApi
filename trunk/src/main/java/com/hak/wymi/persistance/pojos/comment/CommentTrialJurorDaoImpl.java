package com.hak.wymi.persistance.pojos.comment;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("unchecked")
public class CommentTrialJurorDaoImpl implements CommentTrialJurorDao {

    @Value("${jury.timeout}")
    private Long juryTimeout;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(CommentTrialJuror commentTrialJuror) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(commentTrialJuror);
        session.refresh(commentTrialJuror);
        setExpires(commentTrialJuror);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CommentTrialJuror getExistingCurrent(String userName) {
        return setExpires((CommentTrialJuror) sessionFactory
                .getCurrentSession()
                .createQuery("from CommentTrialJuror where user.name=:name and violatedSiteRuleVote is null and isIllegalVote is null order by created")
                .setParameter("name", userName)
                .setMaxResults(1)
                .uniqueResult());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void clearExpired() {
        sessionFactory
                .getCurrentSession()
                .createQuery("delete from CommentTrialJuror \n" +
                        "    where violatedSiteRuleVote is null \n" +
                        "    and isIllegalVote is null \n" +
                        "    and UNIX_TIMESTAMP(created) + :timeout < UNIX_TIMESTAMP(current_timestamp())")
                .setParameter("timeout", juryTimeout)
                .executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CommentTrialJuror get(Integer commentTrialJurorId) {
        return (CommentTrialJuror) sessionFactory
                .getCurrentSession()
                .load(CommentTrialJuror.class, commentTrialJurorId);
    }

    private CommentTrialJuror setExpires(CommentTrialJuror commentTrialJuror) {
        if (commentTrialJuror != null) {
            commentTrialJuror.setExpires(commentTrialJuror.getCreated().plusSeconds(juryTimeout.intValue()));
        }
        return commentTrialJuror;
    }
}
