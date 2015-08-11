package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.post.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.posttransaction.PostTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.security.Principal;

@RestController
@RequestMapping(value = "/topic/{topicName}/post/{postId}")
public class PostTransactionController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostTransactionDao postTransactionDao;

    @RequestMapping(
            value = "/donation",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity createPostTransaction(
            Principal principal,
            @RequestBody PostTransaction postTransaction,
            @PathVariable Integer postId) throws ValidationException {

        User user = userDao.get(principal);
        if (user != null) {
            Post post = postDao.get(postId);
            if (post != null) {
                postTransaction.setPost(post);
                postTransaction.setSourceUser(user);

                postTransactionDao.save(postTransaction);

                return new ResponseEntity(HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
