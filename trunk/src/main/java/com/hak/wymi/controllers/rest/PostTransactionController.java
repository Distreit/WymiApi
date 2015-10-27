package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.pojos.balancetransaction.SecureBalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.post.PostDonation;
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
    private PostDonationManager postDonationManager;

    @RequestMapping(value = "/donation", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity createPostTransaction(
            Principal principal,
            @RequestBody PostDonation postDonation,
            @PathVariable Integer postId) throws InvalidValueException {

        postDonationManager.save(postDonation, principal.getName(), postId);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureBalanceTransaction(postDonation)), HttpStatus.ACCEPTED);
    }
}
