package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.topic.SecureTopic;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
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
        topic.setSubscriberCount(0);
        topic.setFilterCount(0);

        if (topicDao.save(topic)) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(topic)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.PATCH, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> patchTopic(@Validated({Update.class}) @RequestBody Topic topic, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final Topic freshTopic = topicDao.update(topic, principal);
        if (freshTopic != null) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(freshTopic)), HttpStatus.CREATED);
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

    @RequestMapping(value = "{topicName}/{type}", method = RequestMethod.PUT, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> addSubscriber(@PathVariable String topicName,
                                                           @PathVariable String type,
                                                           Principal principal) {
        if (type.equals("subscribers")) {
            return removeOrAddSubscriber(principal, topicName, false, true);
        } else if (type.equals("filters")) {
            return removeOrAddSubscriber(principal, topicName, false, false);
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "{topicName}/{type}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> removeSubscriber(@PathVariable String topicName,
                                                              @PathVariable String type,
                                                              Principal principal) {
        if (type.equals("subscribers")) {
            return removeOrAddSubscriber(principal, topicName, true, true);
        } else if (type.equals("filters")) {
            return removeOrAddSubscriber(principal, topicName, true, false);
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<UniversalResponse> removeOrAddSubscriber(Principal principal, String topicName, boolean remove, boolean isSubscription) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userDao.get(principal);
        if (user != null) {
            final Topic topic = topicDao.get(topicName);
            if (topic != null) {
                if (((isSubscription && ((remove && topic.removeSubscriber(user)) || (!remove && topic.addSubscriber(user))))
                        || (!isSubscription && ((remove && topic.removeFilter(user)) || (!remove && topic.addFilter(user)))))
                        && topicDao.update(topic)) {
                    final SecureToSend secureTopics = new SecureTopic(topic);
                    return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
                }
            }
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
