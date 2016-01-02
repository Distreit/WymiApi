package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicManager {
    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    public Topic get(String topicName) {
        return topicDao.get(topicName);
    }

    @Transactional
    public Topic update(Topic topic, String userName) {
        Topic persistentTopic = topicDao.get(topic.getName());

        if (persistentTopic.getOwner().getName().equals(userName)) {
            persistentTopic.setTitle(topic.getTitle());
            persistentTopic.setDescription(topic.getDescription());
            persistentTopic.setFeePercent(topic.getFeePercent());
            persistentTopic.setFeeFlat(topic.getFeeFlat());

            topicDao.update(persistentTopic);

            return persistentTopic;
        } else {
            throw new UnsupportedOperationException("User does not have update access to this topic.");
        }
    }

    @Transactional
    public void save(Topic topic) {
        topicDao.save(topic);
    }

    @Transactional
    public List<Topic> getAll(int firstResult, int maxResults) {
        return topicDao.getAll(firstResult, maxResults);
    }

    @Transactional
    public List<Topic> getRentDue() {
        return topicDao.getRentDue();
    }

    @Transactional
    public void removeOrAddSubscriber(String userName, String topicName, boolean remove, boolean isSubscription) {
        final User user = userDao.getFromName(userName);
        final Topic topic = topicDao.get(topicName);

        if (isSubscription) {
            if (remove) {
                topic.removeSubscriber(user);
            } else {
                topic.addSubscriber(user);
            }
        } else {
            if (remove) {
                topic.removeFilter(user);
            } else {
                topic.addFilter(user);
            }
        }

        topicDao.update(topic);
    }
}
