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
        final Session session = sessionFactory.getCurrentSession();
        final List<Comment> commentList = session
                .createQuery("from Comment where post.postId=:postId and parentComment=null ORDER BY score DESC")
                .setParameter("postId", postId)
                .list();
        return commentList;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Comment get(Integer commentId) {
        final Session session = sessionFactory.getCurrentSession();
        final Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId")
                .setParameter("commentId", commentId)
                .uniqueResult();
        return comment;
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
    public boolean delete(Integer commentId, Principal principal) {
        final Session session = sessionFactory.getCurrentSession();
        final Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId and author.name=:authorName")
                .setParameter("commentId", commentId)
                .setParameter("authorName", principal.getName())
                .uniqueResult();
        comment.setDeleted(Boolean.TRUE);
        comment.setContent("");
        session.update(comment);
        return true;
    }
}
