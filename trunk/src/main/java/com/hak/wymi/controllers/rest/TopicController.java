package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.secure.SecureTopic;
import com.hak.wymi.persistance.pojos.unsecure.Topic;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.TopicDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;
import com.hak.wymi.validations.groups.Creation;
import com.hak.wymi.validations.groups.Update;
import org.apache.commons.lang3.time.DateUtils;
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
@RequestMapping(value = "/topic")
public class TopicController {
    private static final int THIRTY_DAYS = 30;
    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createTopic(@Validated({Creation.class}) @RequestBody Topic topic, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userDao.get(principal);

        topic.setOwner(user);
        topic.setRent(0);
        topic.setRentDueDate(DateUtils.addDays(new Date(), THIRTY_DAYS));
        topic.setSubscribers(0);
        topic.setUnsubscribers(0);

        if (topicDao.save(topic)) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(topic)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> updateTopic(@Validated({Update.class}) @RequestBody Topic topic, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        if (topicDao.update(topic.getTopicId(), principal) != null) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(topic)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopics() {
        final UniversalResponse universalResponse = new UniversalResponse();
        final List<SecureToSend> secureTopics = topicDao.getAll()
                .stream()
                .map(SecureTopic::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "{topicName}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopic(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final SecureToSend secureTopics = new SecureTopic(topicDao.get(topicName));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }
}
