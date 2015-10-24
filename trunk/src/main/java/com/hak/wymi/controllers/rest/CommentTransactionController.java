package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.pojos.balancetransaction.SecureBalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.validations.groups.Creation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/comment/{commentId}")
public class CommentTransactionController {
    @Autowired
    private CommentDonationManager commentDonationManager;

    @RequestMapping(value = "/donation", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createCommentTransaction(
            Principal principal,
            @Validated(Creation.class) @RequestBody CommentDonation commentDonation,
            @PathVariable Integer commentId
    ) throws InvalidValueException {
        commentDonationManager.save(commentDonation, principal.getName(), commentId);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureBalanceTransaction(commentDonation)), HttpStatus.ACCEPTED);
    }
}
