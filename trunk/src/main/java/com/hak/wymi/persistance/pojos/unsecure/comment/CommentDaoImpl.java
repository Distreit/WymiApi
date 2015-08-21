package com.hak.wymi.persistance.pojos.unsecure.comment;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentDaoImpl implements CommentDao {
    protected static final Logger logger = LoggerFactory.getLogger(CommentDaoImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Comment> getAll(Integer postId) {
        Session session = this.sessionFactory.openSession();
        List<Comment> commentList = session
                .createQuery("from Comment where post.postId=:postId and parentComment=null")
                .setParameter("postId", postId)
                .list();
        session.close();
        return commentList;
    }

    @Override
    public Boolean save(Comment comment) {
        return saveOrUpdate(comment, true);
    }

    @Override
    public Comment get(Integer commentId) {
        Session session = this.sessionFactory.openSession();
        Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId")
                .setParameter("commentId", commentId)
                .uniqueResult();
        session.close();
        return comment;
    }

    private boolean saveOrUpdate(Comment comment, boolean save) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            if (save) {
                session.persist(comment);
            } else {
                session.update(comment);
            }
            tx.commit();
            return true;
        } catch (HibernateException e) {
            logger.error(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
}
