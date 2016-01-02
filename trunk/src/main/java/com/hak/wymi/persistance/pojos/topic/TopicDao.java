package com.hak.wymi.persistance.pojos.topic;

import java.util.List;

public interface TopicDao {
    void save(Topic topic);

    Topic get(String name);

    boolean update(Topic topic);

    List<Topic> getAll(int firstResult, int maxResults);

    List<Topic> getRentDue();
}
