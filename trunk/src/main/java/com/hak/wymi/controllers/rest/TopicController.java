package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.topic.SecureTopic;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;
import com.hak.wymi.validations.groups.Update;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/topic")
public class TopicController {
    private static final int THIRTY_DAYS = 30;
    private static final String SUBSCRIBERS = "subscribers";
    private static final String FILTERS = "filters";
    @Autowired
    private TopicManager topicManager;

    @Autowired
    private UserManager userManager;

    @Value("${topic.max.results.per.request}")
    private Integer maxResultsPerRequest;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createTopic(@Validated({Creation.class}) @RequestBody Topic topic, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userManager.get(principal);

        topic.setOwner(user);
        topic.setRent(0);
        topic.setRentDueDate(new DateTime().plusDays(THIRTY_DAYS));
        topic.setSubscriberCount(0);
        topic.setFilterCount(0);

        if (topicManager.save(topic)) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(topic)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.PATCH, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> patchTopic(@Validated({Update.class}) @RequestBody Topic topic, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final Topic freshTopic = topicManager.update(topic, principal);
        if (freshTopic != null) {
            return new ResponseEntity<>(universalResponse.setData(new SecureTopic(freshTopic)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopics(
            @RequestParam(required = false, defaultValue = "0") Integer firstResult,
            @RequestParam(required = false, defaultValue = "25") Integer maxResults) {

        final UniversalResponse universalResponse = new UniversalResponse();
        final List<SecureToSend> secureTopics = topicManager.getAll(firstResult, Math.min(maxResultsPerRequest, maxResults))
                .stream()
                .map(SecureTopic::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "{topicName}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopic(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final SecureToSend secureTopics = new SecureTopic(topicManager.get(topicName));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "{topicName}/{type}", method = RequestMethod.PUT, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> addSubscriber(@PathVariable String topicName,
                                                           @PathVariable String type,
                                                           Principal principal) {
        if (SUBSCRIBERS.equals(type)) {
            return removeOrAddSubscriber(principal, topicName, false, true);
        } else if (FILTERS.equals(type)) {
            return removeOrAddSubscriber(principal, topicName, false, false);
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "{topicName}/{type}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> removeSubscriber(@PathVariable String topicName,
                                                              @PathVariable String type,
                                                              Principal principal) {
        if (SUBSCRIBERS.equals(type)) {
            return removeOrAddSubscriber(principal, topicName, true, true);
        } else if (FILTERS.equals(type)) {
            return removeOrAddSubscriber(principal, topicName, true, false);
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<UniversalResponse> removeOrAddSubscriber(Principal principal, String topicName, boolean remove, boolean isSubscription) {
        final User user = userManager.get(principal);
        if (user != null) {
            final Topic topic = topicManager.get(topicName);
            if (topic != null) {
                return removeOrAddSubscriber(user, topic, remove, isSubscription);
            }
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<UniversalResponse> removeOrAddSubscriber(User user, Topic topic, boolean remove, boolean isSubscription) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final boolean needsSave;
        if (isSubscription) {
            if (remove) {
                needsSave = topic.removeSubscriber(user);
            } else {
                needsSave = topic.addSubscriber(user);
            }
        } else {
            if (remove) {
                needsSave = topic.removeFilter(user);
            } else {
                needsSave = topic.addFilter(user);
            }
        }

        if (needsSave && topicManager.update(topic)) {
            final SecureToSend secureTopics = new SecureTopic(topic);
            return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(new UniversalResponse().addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
