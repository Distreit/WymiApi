package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.topicBid.TopicBid;
import com.hak.wymi.persistance.pojos.topicBid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicBid.TopicBidDao;
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

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createComment(
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

            if (topicBidDao.save(topicBidCreation)) {
                balanceTransactionManager.addToProcessQueue(topicBidCreation);
                return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
