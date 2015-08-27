package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecureComment;
import com.hak.wymi.persistance.pojos.unsecure.Comment;
import com.hak.wymi.persistance.pojos.unsecure.dao.CommentDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "post/{postId}")
public class CommentController {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @RequestMapping(
            value = "/comment",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8"
    )
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureComment>> createComment(
            Principal principal,
            @Validated({Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId
    ) {
        if (saveNewComment(comment, principal, postId, null)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/comment/{parentCommentId}",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8"
    )
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureComment>> createChildComment(
            Principal principal,
            @Validated({Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId,
            @PathVariable Integer parentCommentId
    ) {
        final Comment parentComment = commentDao.get(parentCommentId);
        if (parentComment != null && saveNewComment(comment, principal, postId, parentComment)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean saveNewComment(Comment comment, Principal principal, Integer postId, Comment parentComment) {
        comment.setAuthor(userDao.get(principal));
        comment.setPost(postDao.get(postId));
        comment.setParentComment(parentComment);
        comment.setDeleted(Boolean.FALSE);
        comment.setPoints(0);
        return commentDao.save(comment);
    }

    @RequestMapping(
            value = "/comment",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8"
    )
    public ResponseEntity<List<SecureComment>> getComments(@PathVariable Integer postId) {
        final List<SecureComment> comments = commentDao.getAll(postId).stream().map(SecureComment::new).collect(Collectors.toCollection(() -> new LinkedList<>()));
        return new ResponseEntity<>(comments, HttpStatus.ACCEPTED);
    }

    @RequestMapping(
            value = "/comment/{commentId}",
            method = RequestMethod.DELETE,
            produces = "application/json; charset=utf-8"
    )
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureComment>> deleteComments(@PathVariable Integer commentId, Principal principal) {
        if (commentDao.delete(commentId, principal)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
