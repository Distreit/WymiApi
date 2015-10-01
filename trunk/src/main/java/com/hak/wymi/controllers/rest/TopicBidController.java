package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicbid.SecureTopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidDao;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.BalanceTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/topic/{topicName}/bid")
public class TopicBidController {
    @Autowired
    private TopicBidDao topicBidDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopicBidsForTopic(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final List<SecureToSend> secureTopicBids = topicBidDao.get(topicName, TopicBidState.WAITING)
                .stream()
                .map(SecureTopicBid::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(secureTopicBids), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{topicBidId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> cancelTopicBid(@PathVariable Integer topicBidId, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final TopicBidCreation topicBidCreation = topicBidDao.getTransaction(topicBidId);
        if (topicBidCreation != null) {
            final SecureTopicBid secureTopicBid = new SecureTopicBid(topicBidCreation.getTopicBid());
            final User user = userDao.get(principal);
            if (balanceTransactionManager.cancel(user, topicBidCreation)) {
                return new ResponseEntity<>(universalResponse.setData(secureTopicBid), HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createTopicBid(
            Principal principal,
            @RequestBody Integer amount,
            @PathVariable String topicName
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();
        if (amount != null && amount > 0) {
            final User user = userDao.get(principal);
            final Topic topic = topicDao.get(topicName);

            final TopicBid topicBid = new TopicBid();
            topicBid.setUser(user);
            topicBid.setTopic(topic);
            topicBid.setCurrentBalance(0);

            final TopicBidCreation topicBidCreation = new TopicBidCreation();
            topicBidCreation.setTopicBid(topicBid);
            topicBidCreation.setAmount(amount);

            if (topicBidDao.save(topicBidCreation) && balanceTransactionManager.process(topicBidCreation)) {
                final SecureToSend secureTopicBid = new SecureTopicBid(topicBidCreation.getTopicBid());
                return new ResponseEntity<>(universalResponse.setData(secureTopicBid), HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
