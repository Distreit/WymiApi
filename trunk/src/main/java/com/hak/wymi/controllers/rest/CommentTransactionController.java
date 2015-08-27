package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;
import com.hak.wymi.persistance.pojos.unsecure.comment.CommentDao;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionAbstract;
import com.hak.wymi.persistance.pojos.unsecure.commenttransaction.CommentTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import com.hak.wymi.utility.BalanceTransactionManager;
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
    private BalanceTransactionManager balanceTransactionManager;

    @Autowired
    private CommentTransactionDao commentTransactionDao;

    @RequestMapping(
            value = "/donation",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity createCommentTransaction(
            Principal principal,
            @Validated(Creation.class) @RequestBody CommentTransactionAbstract commentTransaction,
            @PathVariable Integer commentId) throws IllegalArgumentException {

        final User user = userDao.get(principal);
        final Comment comment = commentDao.get(commentId);

        if (user != null && comment != null) {
            if (comment.getAuthorId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Cannot donate to your own comment.");
            }

            commentTransaction.setComment(comment);
            commentTransaction.setSourceUser(user);

            commentTransactionDao.save(commentTransaction);

            balanceTransactionManager.add(commentTransaction);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
