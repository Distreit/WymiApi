package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.CommentCreationManager;
import com.hak.wymi.persistance.managers.CommentManager;
import com.hak.wymi.persistance.managers.PostManger;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.SecureComment;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "post/{postId}/comment")
public class CommentController {
    @Autowired
    private CommentManager commentManager;

    @Autowired
    private CommentCreationManager commentCreationManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PostManger postManger;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createComment(
            Principal principal,
            @Validated({Creation.class}) @RequestBody CommentAndTransaction commentAndTransaction,
            @PathVariable Integer postId
    ) throws InsufficientFundsException, InvalidValueException {
        final UniversalResponse universalResponse = new UniversalResponse();
        saveNewComment(commentAndTransaction, principal, postId, null);
        return new ResponseEntity<>(universalResponse.setData(new SecureComment(commentAndTransaction.getComment())), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{parentCommentId}", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createChildComment(
            Principal principal,
            @Validated({Creation.class}) @RequestBody CommentAndTransaction commentAndTransaction,
            @PathVariable Integer postId,
            @PathVariable Integer parentCommentId
    ) throws InsufficientFundsException, InvalidValueException {
        final UniversalResponse universalResponse = new UniversalResponse();

        final Comment parentComment = commentManager.get(parentCommentId);
        if (parentComment != null) {
            saveNewComment(commentAndTransaction, principal, postId, parentComment);
            return new ResponseEntity<>(universalResponse.setData(new SecureComment(commentAndTransaction.getComment())), HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void saveNewComment(CommentAndTransaction commentAndTransaction, Principal principal, Integer postId, Comment parentComment) throws InsufficientFundsException, InvalidValueException {
        final User user = userManager.get(principal);
        final Post post = postManger.get(postId);
        final Topic topic = post.getTopic();

        final CommentCreation commentCreation = commentAndTransaction.getCommentCreation();

        if (topic.getFeeFlat().equals(commentCreation.getFeeFlat()) && topic.getFeePercent().equals(commentCreation.getFeePercent())) {
            final Comment comment = commentAndTransaction.getComment();

            final CommentCreation transaction = new CommentCreation();
            transaction.setFeePercent(topic.getFeePercent());
            transaction.setFeeFlat(topic.getFeeFlat());
            transaction.setState(TransactionState.UNPROCESSED);
            transaction.setComment(comment);

            comment.setAuthor(user);
            comment.setPost(post);
            comment.setParentComment(parentComment);
            comment.setDeleted(Boolean.FALSE);
            comment.setPoints(0);
            comment.setDonations(0);
            commentCreationManager.save(transaction);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getComments(@PathVariable Integer postId) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<SecureToSend> comments = commentManager.getAll(postId)
                .stream()
                .map(SecureComment::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(comments), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> deleteComments(@PathVariable Integer commentId, Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        if (commentManager.delete(commentId, principal)) {
            return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static class CommentAndTransaction {
        @NotNull
        @Valid
        private Comment comment;

        @NotNull
        @Valid
        private CommentCreation commentCreation;

        public CommentCreation getCommentCreation() {
            return commentCreation;
        }

        public void setCommentCreation(CommentCreation commentCreation) {
            this.commentCreation = commentCreation;
        }

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }
    }
}
