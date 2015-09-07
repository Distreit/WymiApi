package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentDonationDaoImpl implements CommentDonationDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(CommentDonation commentDonation) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            commentDonation.setState(TransactionState.UNPROCESSED);
            session.persist(commentDonation);
            session.refresh(commentDonation);
            return true;
        });
    }

    @Override
    public List<CommentDonation> getUnprocessed() {
        final Session session = sessionFactory.openSession();
        final List<CommentDonation> commentDonationList = session
                .createQuery("from CommentDonation p where p.state=:state")
                .setParameter("state", TransactionState.UNPROCESSED)
                .list();
        session.close();
        return commentDonationList;
    }
}
