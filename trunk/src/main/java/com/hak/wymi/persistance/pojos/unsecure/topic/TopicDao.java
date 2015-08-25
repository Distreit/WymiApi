package com.hak.wymi.persistance.pojos.unsecure.topic;

import java.util.List;

public interface TopicDao {
    public boolean save(Topic topic);

    public Topic get(String name);

    public boolean update(Topic topic);

    public List<Topic> getAll();
}
