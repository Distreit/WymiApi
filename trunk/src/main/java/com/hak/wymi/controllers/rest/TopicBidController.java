package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.OwnershipTransactionManager;
import com.hak.wymi.persistance.managers.TopicBidManager;
import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.ownershiptransaction.OwnershipTransaction;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topicbid.SecureTopicBid;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidCreation;
import com.hak.wymi.persistance.pojos.topicbid.TopicBidState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private TopicBidManager topicBidManager;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private OwnershipTransactionManager ownershipTransactionManager;

    @Value("${topic.ownership.ownerBidMultiplier}")
    private double ownerBidMultiplier;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getTopicBidsForTopic(@PathVariable String topicName) {
        final List<SecureToSend> secureTopicBids = topicBidManager
                .get(topicName, TopicBidState.WAITING)
                .stream()
                .map(SecureTopicBid::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(new UniversalResponse().setData(secureTopicBids), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{topicBidId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> cancelTopicBid(@PathVariable Integer topicBidId, Principal principal)
            throws InvalidValueException {
        final SecureToSend topicBid = topicBidManager.cancel(topicBidId, principal.getName()).getSecureTopicBid();
        return new ResponseEntity<>(new UniversalResponse().setData(topicBid), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createTopicBid(
            Principal principal,
            @RequestBody Integer amount,
            @PathVariable String topicName
    ) throws InvalidValueException {
        final TopicBidCreation topicBidCreation = topicBidManager.create(topicName, principal.getName(), amount);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureTopicBid(topicBidCreation.getTopicBid())), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/claim-amount", method = RequestMethod.GET, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> getClaimAmount(@PathVariable String topicName, Principal principal) {
        final Topic topic = topicManager.get(topicName);
        final OwnershipTransaction ownershipTransaction = ownershipTransactionManager.getRentPeriodNotExpired(topic);

        final int claimAmount;
        if (ownershipTransaction.getWinningBid().getUser().getName().equalsIgnoreCase(principal.getName())) {
            claimAmount = 0;
        } else {
            claimAmount = ownershipTransactionManager.getClaimAmount(ownershipTransaction);
        }

        return new ResponseEntity<>(new UniversalResponse().setData(claimAmount), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/claim", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> reclaimTopic(
            Principal principal,
            @RequestBody Integer amount,
            @PathVariable String topicName
    ) throws InvalidValueException {
        final UniversalResponse universalResponse = new UniversalResponse();

        ownershipTransactionManager.claim(topicName, principal.getName(), amount);
        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }
}
