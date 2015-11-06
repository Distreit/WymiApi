package com.hak.wymi.persistance.pojos.post;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("unchecked")
public class PostTrialJurorDaoImpl implements PostTrialJurorDao {

    @Value("${jury.timeout}")
    private Long juryTimeout;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(PostTrialJuror postTrialJuror) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(postTrialJuror);
        session.refresh(postTrialJuror);
        setExpires(postTrialJuror);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PostTrialJuror getExistingCurrent(String userName) {
        return setExpires((PostTrialJuror) sessionFactory
                .getCurrentSession()
                .createQuery("from PostTrialJuror where user.name=:name and violatedSiteRuleVote is null and isIllegalVote is null order by created")
                .setParameter("name", userName)
                .setMaxResults(1)
                .uniqueResult());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void clearExpired() {
        sessionFactory
                .getCurrentSession()
                .createQuery("delete from PostTrialJuror \n" +
                        "    where violatedSiteRuleVote is null \n" +
                        "    and isIllegalVote is null \n" +
                        "    and UNIX_TIMESTAMP(created) + :timeout < UNIX_TIMESTAMP(current_timestamp())")
                .setParameter("timeout", juryTimeout)
                .executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PostTrialJuror get(Integer postTrialJurorId) {
        return (PostTrialJuror) sessionFactory
                .getCurrentSession()
                .load(PostTrialJuror.class, postTrialJurorId);
    }

    private PostTrialJuror setExpires(PostTrialJuror postTrialJuror) {
        if (postTrialJuror != null) {
            postTrialJuror.setExpires(postTrialJuror.getCreated().plusSeconds(juryTimeout.intValue()));
        }
        return postTrialJuror;
    }
}
