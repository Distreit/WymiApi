package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostManger {
    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    @Value("${score.baseTime}")
    private Integer baseTime;

    @Value("${site.domain}")
    private String siteDomain;

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

    @Transactional
    public void updateTrashed(int postId, boolean trashed, String userName) {
        final Post post = get(postId);
        if (post.getTopic().getOwner().getName().equals(userName) && post.getTrashed() != trashed) {
            if (!trashed) {
                post.setScore(post.getScore() - post.getBase() + getBaseTime());
            }
            post.setTrashed(trashed);
            postDao.update(post);
        } else {
            throw new UnsupportedOperationException("User is not authorized to update trashed status of post.");
        }
    }

    @Transactional
    public void delete(int postId, String userName) {
        final Post post = get(postId);
        if (!post.getDeleted() && post.getUser().getName().equals(userName)) {
            post.setDeleted(true);
            post.setTitle("DELETED");
            post.setHref("http://siteDomain");
            post.setText("DELETED");
            postDao.update(post);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public double getBaseTime() {
        return new DateTime().getMillis() / MILLISECONDS_IN_A_SECOND - baseTime;
    }
}
