package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostCreationImpl implements PostCreationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean save(PostCreation postCreation) {
        final Session session = sessionFactory.getCurrentSession();
        postCreation.setState(TransactionState.UNPROCESSED);
        session.save(postCreation.getPost());
        session.refresh(postCreation.getPost());
        postCreation.setPostId(postCreation.getPost().getPostId());

        session.save(postCreation);
        session.flush();
        session.refresh(postCreation);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<PostCreation> getUnprocessed() {
        return sessionFactory.getCurrentSession()
                .createQuery("from PostCreation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
    }
}
