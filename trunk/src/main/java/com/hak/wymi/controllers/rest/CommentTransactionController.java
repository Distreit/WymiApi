package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.unsecure.Comment;
import com.hak.wymi.persistance.pojos.unsecure.CommentTransaction;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.BalanceDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.persistance.utility.BalanceTransactionManager;
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
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private BalanceDao balanceDao;

    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @Autowired
    private CommentTransactionDao commentTransactionDao;

    @RequestMapping(value = "/donation", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createCommentTransaction(
            Principal principal,
            @Validated(Creation.class) @RequestBody CommentTransaction commentTransaction,
            @PathVariable Integer commentId
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final User user = userDao.get(principal);
        final Comment comment = commentDao.get(commentId);

        if (user != null && comment != null) {
            if (comment.getAuthorId().equals(user.getUserId())) {
                return new ResponseEntity<>(universalResponse.addError("Cannot donate to your own comment."), HttpStatus.BAD_REQUEST);
            }

            commentTransaction.setComment(comment);
            commentTransaction.setSourceUser(user);

            commentTransactionDao.save(commentTransaction);

            balanceTransactionManager.add(commentTransaction);

            universalResponse.addTransactions(principal, user, balanceTransactionManager, balanceDao);

            return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
