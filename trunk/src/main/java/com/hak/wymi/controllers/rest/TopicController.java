package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecureTopic;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
public class TopicController extends BaseController {
    @Autowired
    TopicDao topicDao;

    @Autowired
    UserDao userDao;

    @RequestMapping(
            value = "/topic",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Topic> createTopic(@Validated({Topic.Creation.class}) @RequestBody Topic topic, Principal principal) {
        User user = userDao.get(principal);

        topic.setOwner(user);
        topic.setRent(0);
        topic.setRentDueDate(DateUtils.addDays(new Date(), 30));
        topic.setSubscribers(0);
        topic.setUnsubscribers(0);

        if (topicDao.save(topic)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/topic",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<List<SecureTopic>> getTopics() {
        List<Topic> topics = topicDao.getAll();
        List<SecureTopic> secureTopics = topics.stream().map(SecureTopic::new).collect(Collectors.toCollection(() -> new LinkedList<>()));

        return new ResponseEntity<>(secureTopics, HttpStatus.ACCEPTED);
    }
}
