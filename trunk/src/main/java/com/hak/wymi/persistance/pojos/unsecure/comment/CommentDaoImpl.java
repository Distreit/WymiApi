package com.hak.wymi.persistance.pojos.unsecure.comment;

import com.hak.wymi.utility.DaoHelper;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class CommentDaoImpl implements CommentDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Comment> getAll(Integer postId) {
        Session session = sessionFactory.openSession();
        List<Comment> commentList = session
                .createQuery("from Comment where post.postId=:postId and parentComment=null")
                .setParameter("postId", postId)
                .list();
        session.close();
        return commentList;
    }

    @Override
    public Comment get(Integer commentId) {
        Session session = sessionFactory.openSession();
        Comment comment = (Comment) session
                .createQuery("from Comment where commentId=:commentId")
                .setParameter("commentId", commentId)
                .uniqueResult();
        session.close();
        return comment;
    }

    @Override
    public Boolean save(Comment comment) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> session.save(comment));
    }

    @Override
    public boolean delete(Integer commentId, Principal principal) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> {
            Comment comment = (Comment) session
                    .createQuery("from Comment where commentId=:commentId and author.name=:authorName")
                    .setParameter("commentId", commentId)
                    .setParameter("authorName", principal.getName())
                    .uniqueResult();
            comment.setDeleted(true);
            comment.setContent("");
            session.update(comment);
        });
    }
}
