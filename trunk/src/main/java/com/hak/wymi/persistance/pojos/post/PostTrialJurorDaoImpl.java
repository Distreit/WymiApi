package com.hak.wymi.persistance.pojos.post;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostTrialJurorDaoImpl implements PostTrialJurorDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(PostTrialJuror postTrialJuror) {
        sessionFactory.getCurrentSession().save(postTrialJuror);
    }

    @Override
    public List<PostTrialJuror> get(PostTrial postTrial) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(PostTrialJuror.class)
                .add(Restrictions.eq("postId", postTrial.getPostId()))
                .list();
    }
}
