package com.hak.wymi.persistance.pojos.usertopicrank;

import com.hak.wymi.persistance.pojos.AbstractPersistentObject;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "UserTopicRank")
public class UserTopicRank extends AbstractPersistentObject {
    @EmbeddedId()
    private UserTopic userTopic;

    private Double rank;

    public UserTopicRank() {
        super();
    }

    public UserTopicRank(User user, Topic topic, Double rank) {
        this.userTopic = new UserTopic(user, topic);
        this.rank = rank;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Topic getTopic() {
        if (userTopic == null) {
            userTopic = new UserTopic();
        }
        return userTopic.getTopic();
    }

    public void setTopic(Topic topic) {
        if (userTopic == null) {
            userTopic = new UserTopic();
        }
        this.userTopic.setTopic(topic);
    }

    public User getUser() {
        if (userTopic == null) {
            userTopic = new UserTopic();
        }
        return userTopic.getUser();
    }

    public void setUser(User user) {
        if (userTopic == null) {
            userTopic = new UserTopic();
        }
        this.userTopic.setUser(user);
    }

    @Embeddable
    public static class UserTopic implements Serializable {
        @ManyToOne
        @JoinColumn(name = "topicId")
        protected Topic topic;

        @ManyToOne
        @JoinColumn(name = "userId")
        protected User user;

        public UserTopic() {
            super();
        }

        public UserTopic(User user, Topic topic) {
            this.user = user;
            this.topic = topic;
        }

        private boolean isValid() {
            return this.user != null && this.topic != null;
        }

//        @Override
//        public int hashCode() {
//            return user.hashCode() + topic.hashCode();
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (obj instanceof UserTopic) {
//                UserTopic that = (UserTopic) obj;
//                if (this.isValid() && that.isValid()) {
//                    return this.user.getUserId().equals(that.user.getUserId())
//                            && this.topic.getTopicId().equals(that.topic.getTopicId());
//                }
//            }
//            return false;
//        }

        public Topic getTopic() {
            return topic;
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}