package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.comment.SecureComment;
import com.hak.wymi.persistance.pojos.post.PostDonation;
import com.hak.wymi.persistance.pojos.post.SecurePost;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user/name/{userName}")
public class UserTransactionsController {
    @Autowired
    private CommentDonationManager commentDonationManager;

    @Autowired
    private PostDonationManager postDonationManager;

    @RequestMapping(value = "/transactions/comment", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getUserCommentTransactions(@PathVariable String userName,
                                                                        @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                                        @RequestParam(required = false, defaultValue = "25") Integer maxResults,
                                                                        Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<CommentDonation> donations;

        if (principal == null || !userName.equals(principal.getName())) {
        } else {
            donations = commentDonationManager.getPrivateTransactions(principal.getName(), firstResult, maxResults);
            universalResponse.setData(donations.stream().map(SecureCommentDonation::new).collect(Collectors.toList()));
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/transactions/post", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getUserPostTransactions(@PathVariable String userName,
                                                                     @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                                     @RequestParam(required = false, defaultValue = "25") Integer maxResults,
                                                                     Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<PostDonation> donations;

        if (principal == null || !userName.equals(principal.getName())) {
        } else {
            donations = postDonationManager.getPrivateTransactions(principal.getName(), firstResult, maxResults);
            universalResponse.setData(donations.stream().map(SecurePostDonation::new).collect(Collectors.toList()));
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }


    private class SecureCommentDonation implements SecureToSend {
        public int amount;
        public SecureComment comment;
        public DateTime date;

        public SecureCommentDonation(CommentDonation commentDonation) {
            this.amount = commentDonation.getAmount();
            this.comment = new SecureComment(commentDonation.getComment());
            this.date = commentDonation.getCreated();
        }
    }

    private class SecurePostDonation implements SecureToSend {
        public int amount;
        public SecurePost post;
        public DateTime date;

        public SecurePostDonation(PostDonation postDonation) {
            this.amount = postDonation.getAmount();
            this.post = new SecurePost(postDonation.getPost());
            this.date = postDonation.getCreated();
        }
    }
}
