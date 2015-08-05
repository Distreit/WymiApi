package com.hak.wymi.persistance.pojos.topic;

import com.hak.wymi.persistance.pojos.topic.Topic;

import java.security.Principal;

public interface TopicDao {
	public boolean save(Topic topic);

	public Topic get(String name);

	public boolean update(Topic topic);
}
