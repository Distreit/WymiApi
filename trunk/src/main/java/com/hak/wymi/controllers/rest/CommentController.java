package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.CommentManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InsufficientFundsException;
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
@RequestMapping(value = "post/{postId}/comment")
public class CommentController {
    @Autowired
    private CommentManager commentManager;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createComment(
            @Validated({Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId,
            @RequestParam(required = true) Integer feeFlat,
            @RequestParam(required = true) Integer feePercent,
            @RequestParam(required = false) Integer parentCommentId,
            Principal principal
    ) throws InsufficientFundsException, InvalidValueException {
        commentManager.create(comment, principal.getName(), postId, feeFlat, feePercent, parentCommentId);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecureComment(comment)), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getComments(@PathVariable Integer postId) {
        final List<SecureToSend> comments = commentManager.getAll(postId)
                .stream()
                .map(SecureComment::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(new UniversalResponse().setData(comments), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> deleteComments(@PathVariable Integer commentId, Principal principal) {
        commentManager.delete(commentId, principal);
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }
}
