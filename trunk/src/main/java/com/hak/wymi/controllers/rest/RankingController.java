package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.comment.SecureCommentDonation;
import com.hak.wymi.persistance.utility.UserTopicRanker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RankingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankingController.class);

    @Autowired
    private CommentDonationDao commentDonationDao;

    @RequestMapping(value = "/ranking/topic/{topicName}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<SecureToSend> posts = commentDonationDao.get(topicName)
                .stream().map(SecureCommentDonation::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(posts), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ranking/topic/{topicName}/ranks", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getRanks(@PathVariable String topicName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<CommentDonation> commentDonations = commentDonationDao.get(topicName);

        final UserTopicRanker ranker = new UserTopicRanker();

        ranker.addDonations(commentDonations);
        Double delta = 1d;
        int iterationCount = 0;
        while (delta > 0.00001 && iterationCount < 1000) {
            delta = ranker.iterate(0.825);
            iterationCount += 1;
            LOGGER.debug("Delta: {}", delta);
        }

        return new ResponseEntity<>(universalResponse.setData(ranker), HttpStatus.ACCEPTED);
    }
}
