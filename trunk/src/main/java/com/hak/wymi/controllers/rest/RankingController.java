package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.managers.UserTopicRankManager;
import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.SecureBalanceTransaction;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/ranking")
public class RankingController {
    @Autowired
    private CommentDonationManager commentDonationManager;

    @Autowired
    private UserTopicRankManager userTopicRankManager;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private PostDonationManager postDonationManager;

    @Value("${ranking.delta}")
    private Double minDelta;

    @Value("${ranking.maxIterations}")
    private Integer maxIterations;

    @Value("${ranking.dampeningFactor}")
    private Double dampeningFactor;

    @RequestMapping(value = "/topic/{topicName}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<SecureToSend> posts = commentDonationManager.get(topicName)
                .stream()
                .map(SecureBalanceTransaction::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(posts), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/topic/{topicName}/ranks", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getRanks(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<? extends DonationTransaction> donations = Stream.concat(
                commentDonationManager.get(topicName).stream(),
                postDonationManager.get(topicName).stream()
        ).collect(Collectors.toList());

        final UserTopicRanker ranker = new UserTopicRanker(topicManager.get(topicName));

        ranker.runOn(donations, minDelta, maxIterations, dampeningFactor);
        userTopicRankManager.save(ranker);

        return new ResponseEntity<>(universalResponse.setData(ranker), HttpStatus.ACCEPTED);
    }
}
