package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class TopicManager {
    @Autowired
    private TopicDao topicDao;

    @Transactional
    public Topic get(String topicName) {
        return topicDao.get(topicName);
    }

    @Transactional
    public boolean update(Topic topic) {
        return topicDao.update(topic);
    }

    @Transactional
    public Topic update(Topic topic, Principal principal) {
        return topicDao.update(topic, principal);
    }

    @Transactional
    public boolean save(Topic topic) {
        return topicDao.save(topic);
    }

    @Transactional
    public List<Topic> getAll() {
        return topicDao.getAll();
    }

    @Transactional
    public List<Topic> getRentDue() {
        return topicDao.getRentDue();
    }
}
