package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.Topic;

import java.security.Principal;
import java.util.List;

public interface TopicDao {
    boolean save(Topic topic);

    Topic get(String name);

    boolean update(Topic topic);

    Topic update(Integer topicId, Principal principal);

    List<Topic> getAll();
}
