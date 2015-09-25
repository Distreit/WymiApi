package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import com.hak.wymi.persistance.pojos.comment.SecureCommentDonation;
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
}
