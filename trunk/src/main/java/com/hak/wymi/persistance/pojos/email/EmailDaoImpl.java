package com.hak.wymi.persistance.pojos.email;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EmailDaoImpl implements EmailDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Email email) {
        sessionFactory.getCurrentSession().save(email);
    }

    @Override
    public Email getUnsent() {
        return null;
    }

    @Override
    public void update(Email email) {
        sessionFactory.getCurrentSession().update(email);
    }
}
