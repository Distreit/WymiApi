package com.hak.wymi.persistance.pojos.unsecure.topic;

import java.util.List;

public interface TopicDao {
    boolean save(Topic topic);

    Topic get(String name);

    boolean update(Topic topic);

    List<Topic> getAll();
}
