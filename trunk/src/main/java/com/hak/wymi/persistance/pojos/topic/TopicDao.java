package com.hak.wymi.persistance.pojos.topic;

import java.security.Principal;
import java.util.List;

public interface TopicDao {
    boolean save(Topic topic);

    Topic get(String name);

    boolean update(Topic topic);

    Topic update(Topic topic, Principal principal);

    List<Topic> getAll(int firstResult, int maxResults);

    List<Topic> getRentDue();
}
