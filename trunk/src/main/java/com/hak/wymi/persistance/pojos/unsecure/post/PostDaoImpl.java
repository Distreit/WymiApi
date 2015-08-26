package com.hak.wymi.persistance.pojos.unsecure.post;

import com.hak.wymi.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Post> getAll(String topicName) {
        Session session = sessionFactory.openSession();
        List<Post> postList = session.createQuery("from Post p where p.topic.name=:topicName")
                .setParameter("topicName", topicName)
                .list();
        session.close();
        return postList;
    }

    @Override
    public Post get(Integer id) {
        if (id != null) {
            Session session = sessionFactory.openSession();
            List<Post> postList = session.createQuery("from Post where postId=:postId")
                    .setParameter("postId", id)
                    .list();
            session.close();
            if (postList.size() == 1) {
                return postList.get(0);
            }
        }
        return null;
    }

    @Override
    @Secured("ROLE_VALIDATED")
    public boolean save(Post post) {
        return DaoHelper.genericTransaction(sessionFactory.openSession(), session -> session.persist(post));
    }
}
