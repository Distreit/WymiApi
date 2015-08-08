package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecurePost;
import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.post.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.topic.Topic;
import com.hak.wymi.persistance.pojos.unsecure.topic.TopicDao;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import com.hak.wymi.utility.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/topic/{topicName}")
public class PostController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private TopicDao topicDao;

    @RequestMapping(
            value = "/post",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Topic> createPost(
            @Validated(Post.Creation.class) @RequestBody Post post,
            @PathVariable String topicName,
            Principal principal) {

        User user = userDao.get(principal);
        Topic topic = topicDao.get(topicName);

        if (user != null && topic != null) {
            post.setTopic(topic);
            post.setUser(user);
            post.setPoints(0);
            long score = new Date().getTime();
            score /= 1000;
            score -= AppConfig.BASE_TIME;
            post.setScore((double) score);

            if (postDao.save(post)) {
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/post",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<List<SecurePost>> getTopics(@PathVariable String topicName) {
        List<Post> posts = postDao.getAll(topicName);
        List<SecurePost> secureTopics = posts.stream().map(SecurePost::new).collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(secureTopics, HttpStatus.ACCEPTED);
    }
}
