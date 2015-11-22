package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.CommentManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.SecureComment;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CommentController {
    @Autowired
    private CommentManager commentManager;

    @RequestMapping(value = {"post/{postId}/comment"}, method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createComment(
            @Validated({Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId,
            @RequestParam(required = true) Integer feeFlat,
            @RequestParam(required = true) Integer feePercent,
            Principal principal
    ) throws InvalidValueException {
        commentManager.create(comment, principal.getName(), postId, feeFlat, feePercent, null);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureComment(comment)), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = {"post/{postId}/comment/{parentCommentId}"}, method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createCommentWithParent(
            @Validated({Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId,
            @RequestParam(required = true) Integer feeFlat,
            @RequestParam(required = true) Integer feePercent,
            @PathVariable Integer parentCommentId,
            Principal principal
    ) throws InvalidValueException {
        commentManager.create(comment, principal.getName(), postId, feeFlat, feePercent, parentCommentId);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureComment(comment)), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "post/{postId}/comment", method = RequestMethod.PUT, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> updateComment(
            @RequestBody Comment comment,
            Principal principal
    ) throws InvalidValueException {
        commentManager.update(comment, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "post/{postId}/comment", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getComments(@PathVariable Integer postId) {
        final List<SecureToSend> comments = commentManager.getAll(postId)
                .stream()
                .map(SecureComment::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(new UniversalResponse().setData(comments), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "post/{postId}/comment/{commentId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> deleteComments(@PathVariable Integer commentId, Principal principal) {
        commentManager.delete(commentId, principal);
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "comment/{commentId}/trashed", method = RequestMethod.PATCH, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> setTrashed(@PathVariable Integer commentId,
                                                        @RequestParam(required = true) Boolean trashed,
                                                        Principal principal) {
        commentManager.updateTrashed(commentId, trashed, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }
}
