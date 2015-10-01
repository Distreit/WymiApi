package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.comment.SecureCommentDonation;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRankDao;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RankingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankingController.class);

    @Autowired
    private CommentDonationDao commentDonationDao;

    @Autowired
    private UserTopicRankDao userTopicRankDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private PostDonationDao postDonationDao;

    @Value("${ranking.delta}")
    private Double minDelta;

    @Value("${ranking.maxIterations}")
    private Integer maxIterations;

    @Value("${ranking.dampeningFactor}")
    private Double dampeningFactor;

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

        final List<? extends DonationTransaction> donations = Stream.concat(
                commentDonationDao.get(topicName).stream(),
                postDonationDao.get(topicName).stream()
        ).collect(Collectors.toList());

        final UserTopicRanker ranker = new UserTopicRanker();

        ranker.addDonations(donations);
        Double delta = 1d;
        int iterationCount = 0;
        long start = System.nanoTime();
        while (delta > minDelta && iterationCount < maxIterations) {
            delta = ranker.iterate(dampeningFactor);
            iterationCount += 1;
        }
        long elapsedTime = System.nanoTime() - start;
        userTopicRankDao.save(ranker, topicDao.get(topicName));
        LOGGER.debug("delta: {}, iterationCount: {}, in: {}", delta, iterationCount, elapsedTime / 1000000000.0);

        return new ResponseEntity<>(universalResponse.setData(ranker), HttpStatus.ACCEPTED);
    }
}
