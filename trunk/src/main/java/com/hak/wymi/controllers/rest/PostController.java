package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.PostManger;
import com.hak.wymi.persistance.managers.TrialManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.SecurePost;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RestController
public class PostController {
    private static final int MAX_RESULTS_PER_REQUEST = 100;

    @Autowired
    private PostManger postManger;

    @Autowired
    private TrialManager trialManager;

    @RequestMapping(value = "/topic/{topicName}/post", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createPost(
            @Validated(Creation.class) @RequestBody Post post,
            @RequestParam(required = true) Integer feeFlat,
            @RequestParam(required = true) Integer feePercent,
            @PathVariable String topicName,
            Principal principal
    ) throws InvalidValueException {
        postManger.create(post, topicName, principal.getName(), feeFlat, feePercent);
        return new ResponseEntity<>(new UniversalResponse().setData(new SecurePost(post)), HttpStatus.ACCEPTED);
    }

//    @RequestMapping(value = "/topic/{topicName}/post", method = RequestMethod.GET, produces = Constants.JSON)
//    public ResponseEntity<UniversalResponse> getPosts(@PathVariable String topicName,
//                                                      @RequestParam(required = false, defaultValue = "0") Integer firstResult,
//                                                      @RequestParam(required = false, defaultValue = "25") Integer maxResults
//    ) {
//        final List<SecureToSend> secureTopics = postManger
//                .get(topicName, firstResult, Math.min(MAX_RESULTS_PER_REQUEST, maxResults))
//                .stream()
//                .map(SecurePost::new)
//                .collect(Collectors.toCollection(LinkedList::new));
//        return new ResponseEntity<>(new UniversalResponse().setData(secureTopics), HttpStatus.ACCEPTED);
//    }

    @RequestMapping(value = "/topic/{topicName}/post/{postId}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@PathVariable Integer postId) {
        return new ResponseEntity<>(
                new UniversalResponse().setData(new SecurePost(postManger.get(postId))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(value = "/post", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@RequestParam(required = true) String topics,
                                                     @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                     @RequestParam(required = false, defaultValue = "25") Integer maxResults,
                                                     @RequestParam(required = false, defaultValue = "false") Boolean filter,
                                                     @RequestParam(required = false, defaultValue = "false") Boolean trashed) {
        final List<String> topicList = new LinkedList<>();
        if (topics != null) {
            topicList.addAll(Arrays.asList(topics.split(",")));
        }
        final List<? extends SecureToSend> posts = postManger
                .getSecure(topicList, firstResult, maxResults, filter, trashed);

        return new ResponseEntity<>(new UniversalResponse().setData(posts), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/post/{postId}/trashed", method = RequestMethod.PATCH, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> setTrashed(@PathVariable Integer postId,
                                                        @RequestParam(required = true) Boolean trashed,
                                                        Principal principal) {
        postManger.updateTrashed(postId, trashed, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/post/{postId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> deletePost(@PathVariable Integer postId, Principal principal) {
        postManger.delete(postId, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/post/{postId}/trial", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> reportPost(@PathVariable Integer postId, Principal principal) {
        trialManager.create(postId, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }
}
