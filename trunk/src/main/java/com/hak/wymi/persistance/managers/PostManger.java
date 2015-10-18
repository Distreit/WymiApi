package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostManger {
    @Autowired
    private PostDao postDao;

    @Transactional
    public List<Post> get(String topicName, Integer firstResult, int min) {
        return postDao.get(topicName, firstResult, min);
    }

    @Transactional
    public Post get(Integer postId) {
        return postDao.get(postId);
    }

    @Transactional
    public List<Post> get(List<String> topicList, Integer firstResult, Integer maxResults, Boolean filter, boolean trashed) {
        return postDao.get(topicList, firstResult, maxResults, filter, trashed);
    }
}
