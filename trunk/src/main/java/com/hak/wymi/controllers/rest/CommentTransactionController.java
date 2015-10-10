package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.CommentManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.TransactionProcessor;
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
    private UserManager userManager;

    @Autowired
    private CommentManager commentManager;

    @Autowired
    private BalanceManager balanceManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private CommentDonationManager commentDonationManager;

    @RequestMapping(value = "/donation", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createCommentTransaction(
            Principal principal,
            @Validated(Creation.class) @RequestBody CommentDonation commentDonation,
            @PathVariable Integer commentId
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final User user = userManager.get(principal);
        final Comment comment = commentManager.get(commentId);

        if (user != null && comment != null) {
            if (comment.getDeleted()) {
                return new ResponseEntity<>(universalResponse.addError("Comment deleted."), HttpStatus.BAD_REQUEST);
            }

            if (comment.getAuthorId().equals(user.getUserId())) {
                return new ResponseEntity<>(universalResponse.addError("Cannot donate to your own comment."), HttpStatus.BAD_REQUEST);
            }

            commentDonation.setComment(comment);
            commentDonation.setSourceUser(user);

            commentDonationManager.save(commentDonation);

            transactionProcessor.add(commentDonation);

            universalResponse.addTransactions(principal, user, transactionProcessor, balanceManager);

            return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
