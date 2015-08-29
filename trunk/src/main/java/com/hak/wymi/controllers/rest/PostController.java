package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.secure.SecurePost;
import com.hak.wymi.persistance.pojos.unsecure.Post;
import com.hak.wymi.persistance.pojos.unsecure.Topic;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.TopicDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;
import com.hak.wymi.utility.AppConfig;
import com.hak.wymi.validations.groups.Creation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/topic/{topicName}")
public class PostController {
    private static final int MILLISECONDS_IN_A_SECOND = 1000;
    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private TopicDao topicDao;

    @RequestMapping(value = "/post", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createPost(
            @Validated(Creation.class) @RequestBody Post post,
            @PathVariable String topicName,
            Principal principal
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userDao.get(principal);
        final Topic topic = topicDao.get(topicName);

        if (user != null && topic != null) {
            post.setTopic(topic);
            post.setUser(user);
            post.setPoints(0);
            long score = new Date().getTime();
            score /= MILLISECONDS_IN_A_SECOND;
            score -= AppConfig.BASE_TIME;
            post.setScore((double) score);

            if (postDao.save(post)) {
                return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/post",
            method = RequestMethod.GET,
            produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPosts(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<Post> posts = postDao.getAll(topicName);
        final List<SecureToSend> secureTopics = posts.stream().map(SecurePost::new).collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }

    @RequestMapping(
            value = "/post/{postId}",
            method = RequestMethod.GET,
            produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@PathVariable Integer postId) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final Post post = postDao.get(postId);
        final SecurePost securePost = new SecurePost(post);

        return new ResponseEntity<>(universalResponse.setData(securePost), HttpStatus.ACCEPTED);
    }
}
