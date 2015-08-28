package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.ErrorList;
import com.hak.wymi.persistance.pojos.unsecure.Post;
import com.hak.wymi.persistance.pojos.unsecure.PostTransaction;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.PostTransactionDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.utility.BalanceTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private BalanceTransactionManager balanceTransactionManager;

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
            @PathVariable Integer postId) {

        final User user = userDao.get(principal);
        if (user != null) {
            final Post post = postDao.get(postId);
            if (post != null) {
                if (post.getUser().getUserId().equals(user.getUserId())) {
                    return new ResponseEntity(new ErrorList("Cannot donate to your own post."), HttpStatus.BAD_REQUEST);
                }
                postTransaction.setPost(post);
                postTransaction.setSourceUser(user);

                postTransactionDao.save(postTransaction);

                balanceTransactionManager.add(postTransaction);
                return new ResponseEntity(HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
