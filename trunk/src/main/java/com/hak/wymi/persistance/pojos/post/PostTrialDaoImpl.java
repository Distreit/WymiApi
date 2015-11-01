package com.hak.wymi.persistance.pojos.post;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostTrialDaoImpl implements PostTrialDao {

    @Autowired
    private SessionFactory sessionFactory;

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
}
