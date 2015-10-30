package com.hak.wymi.persistance.pojos.comment;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentDaoImpl implements CommentDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Comment> getAll(Integer postId) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Comment where post.postId=:postId and parentComment=null ORDER BY score DESC")
                .setParameter("postId", postId)
                .list();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Comment get(Integer commentId) {
        return (Comment) sessionFactory.getCurrentSession().load(Comment.class, commentId);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Boolean save(Comment comment) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(comment);
        session.refresh(comment);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Comment comment) {
        final Session session = sessionFactory.getCurrentSession();
        session.update(comment);

    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(Integer commentId, Principal principal) {
        final Session session = sessionFactory.getCurrentSession();
        final Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId and author.name=:authorName")
                .setParameter("commentId", commentId)
                .setParameter("authorName", principal.getName())
                .uniqueResult();
        comment.setDeleted(Boolean.TRUE);
        comment.setContent("");
        session.update(comment);
    }
}
