package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentDaoImpl implements CommentDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Comment> getAll(Integer postId) {
        final Session session = sessionFactory.openSession();
        final List<Comment> commentList = session
                .createQuery("from Comment where post.postId=:postId and parentComment=null")
                .setParameter("postId", postId)
                .list();
        session.close();
        return commentList;
    }

    @Override
    public Comment get(Integer commentId) {
        final Session session = sessionFactory.openSession();
        final Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId")
                .setParameter("commentId", commentId)
                .uniqueResult();
        session.close();
        return comment;
    }

    @Override
    public Boolean save(Comment comment) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            session.save(comment);
            session.refresh(comment);
            return true;
        });
    }

    @Override
    public boolean delete(Integer commentId, Principal principal) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            final Comment comment = (Comment) session
                    .createQuery("from Comment where commentId=:commentId and author.name=:authorName")
                    .setParameter("commentId", commentId)
                    .setParameter("authorName", principal.getName())
                    .uniqueResult();
            comment.setDeleted(Boolean.TRUE);
            comment.setContent("");
            session.update(comment);
            return true;
        });
    }
}
