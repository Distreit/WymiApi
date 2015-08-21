package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecureComment;
import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;
import com.hak.wymi.persistance.pojos.unsecure.comment.CommentDao;
import com.hak.wymi.persistance.pojos.unsecure.post.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureComment>> createComment(
            Principal principal,
            @Validated({Comment.Creation.class}) @RequestBody Comment comment,
            @PathVariable Integer postId
    ) {
        comment.setAuthor(userDao.get(principal));
        comment.setPost(postDao.get(postId));
        comment.setParentComment(null);
        comment.setDeleted(false);
        comment.setPoints(0);
        if (commentDao.save(comment)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/comment",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<List<SecureComment>> getComments(@PathVariable Integer postId) {
        List<SecureComment> comments = commentDao.getAll(postId).stream().map(SecureComment::new).collect(Collectors.toCollection(() -> new LinkedList<>()));
        return new ResponseEntity<>(comments, HttpStatus.ACCEPTED);
    }

}
