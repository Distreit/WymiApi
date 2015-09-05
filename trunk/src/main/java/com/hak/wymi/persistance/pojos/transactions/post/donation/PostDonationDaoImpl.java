package com.hak.wymi.persistance.pojos.transactions.post.donation;

import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDonationDaoImpl implements PostDonationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(PostDonation postDonation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            postDonation.setState(TransactionState.UNPROCESSED);
            session.persist(postDonation);
            session.refresh(postDonation);
            return true;
        });
    }

    @Override
    public List<PostDonation> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<PostDonation> postDonationList = session
                .createQuery("from PostDonation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return postDonationList;
    }
}
