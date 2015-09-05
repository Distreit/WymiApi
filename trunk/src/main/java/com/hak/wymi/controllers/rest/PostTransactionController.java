package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.balance.BalanceDao;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDao;
import com.hak.wymi.persistance.pojos.transactions.post.PostDonation;
import com.hak.wymi.persistance.pojos.transactions.post.PostDonationDao;
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
@RequestMapping(value = "/topic/{topicName}/post/{postId}")
public class PostTransactionController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private BalanceDao balanceDao;

    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @Autowired
    private PostDonationDao postDonationDao;

    @RequestMapping(value = "/donation", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity createPostTransaction(
            Principal principal,
            @RequestBody PostDonation postDonation,
            @PathVariable Integer postId
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userDao.get(principal);
        if (user != null) {
            final Post post = postDao.get(postId);
            if (post != null) {
                if (post.getUser().getUserId().equals(user.getUserId())) {
                    return new ResponseEntity<>(universalResponse.addError("Cannot donate to your own post."), HttpStatus.BAD_REQUEST);
                }
                postDonation.setPost(post);
                postDonation.setSourceUser(user);

                postDonationDao.save(postDonation);

                balanceTransactionManager.add(postDonation);
                universalResponse.addTransactions(principal, user, balanceTransactionManager, balanceDao);

                return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
