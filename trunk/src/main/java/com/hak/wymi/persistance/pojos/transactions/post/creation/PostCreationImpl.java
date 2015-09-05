package com.hak.wymi.persistance.pojos.transactions.post.creation;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<PostCreation> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<PostCreation> postPostList = session
                .createQuery("from PostCreation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return postPostList;
    }
}
