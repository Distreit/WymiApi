package com.hak.wymi.persistance.pojos.transactions.post.creation;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class PostCreationImpl implements PostCreationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostCreation postCreation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            postCreation.setState(TransactionState.UNPROCESSED);
            session.persist(postCreation.getPost());
            session.refresh(postCreation.getPost());
            session.persist(postCreation);
            session.refresh(postCreation);
            return true;
        });
    }
}
