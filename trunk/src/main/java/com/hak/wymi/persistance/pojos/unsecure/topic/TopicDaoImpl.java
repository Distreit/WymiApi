package com.hak.wymi.persistance.pojos.unsecure.topic;

import com.hak.wymi.utility.DaoHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class TopicDaoImpl implements TopicDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean update(Topic topic) {
        return saveOrUpdate(topic, false);
    }

    @Override
    public List<Topic> getAll() {
        Session session = this.sessionFactory.openSession();
        List<Topic> topicList = session.createQuery("from Topic").list();
        session.close();
        return topicList;
    }

    @Override
    @Secured("ROLE_USER")
    public boolean save(Topic topic) {
        return saveOrUpdate(topic, true);
    }

    private boolean saveOrUpdate(Topic topic, boolean save) {
        return DaoHelper.simpleSaveOrUpdate(topic, this.sessionFactory.openSession(), save);
    }

    @Override
    public Topic get(String name) {
        if (name != null && !"".equals(name)) {
            Session session = this.sessionFactory.openSession();
            List<Topic> topicList = session.createQuery("from Topic where lower(name)=:name")
                    .setParameter("name", name.toLowerCase())
                    .list();
            session.close();
            if (topicList.size() == 1) {
                return topicList.get(0);
            }
        }
        return null;
    }
}
