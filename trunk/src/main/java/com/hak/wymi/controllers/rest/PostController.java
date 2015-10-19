package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.PostCreationManager;
import com.hak.wymi.persistance.managers.PostManger;
import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.SecurePost;
import com.hak.wymi.persistance.pojos.topic.SecureTopic;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.TransactionProcessor;
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

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PostController {
    private static final int MAX_RESULTS_PER_REQUEST = 100;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PostManger postManger;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private PostCreationManager postCreationManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @RequestMapping(value = "/topic/{topicName}/post", method = RequestMethod.POST, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> createPost(
            @Validated(Creation.class) @RequestBody PostAndTransaction postAndTransaction,
            @PathVariable String topicName,
            Principal principal
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userManager.get(principal);
        final Topic topic = topicManager.get(topicName);
        final Post post = postAndTransaction.getPost();
        final PostCreation postCreation = postAndTransaction.getPostCreation();


        if (user != null
                && topic != null
                && postCreation.getFeePercent().equals(topic.getFeePercent())
                && postCreation.getFeeFlat().equals(topic.getFeeFlat())) {

            final PostCreation transaction = new PostCreation();
            transaction.setState(TransactionState.UNPROCESSED);
            transaction.setFeeFlat(topic.getFeeFlat());
            transaction.setFeePercent(topic.getFeePercent());
            transaction.setPost(post);

            post.setTopic(topic);
            post.setUser(user);
            post.setBase(postManger.getBaseTime());
            post.setPoints(0);
            post.setDonations(0);
            post.setScore(post.getBase());

            if (postCreationManager.save(transaction) && transactionProcessor.process(transaction)) {
                return new ResponseEntity<>(universalResponse.setData(new SecurePost(post)), HttpStatus.ACCEPTED);
            }

        } else if (topic != null) {
            return new ResponseEntity<>(universalResponse
                    .setData(new SecureTopic(topic))
                    .addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/topic/{topicName}/post", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPosts(@PathVariable String topicName,
                                                      @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                      @RequestParam(required = false, defaultValue = "25") Integer maxResults
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<Post> posts = postManger.get(topicName, firstResult, Math.min(MAX_RESULTS_PER_REQUEST, maxResults));
        final List<SecureToSend> secureTopics = posts.stream().map(SecurePost::new).collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(secureTopics), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/topic/{topicName}/post/{postId}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@PathVariable Integer postId) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final Post post = postManger.get(postId);
        final SecurePost securePost = new SecurePost(post);

        return new ResponseEntity<>(universalResponse.setData(securePost), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/post", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getPost(@RequestParam(required = false) String topics,
                                                     @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                     @RequestParam(required = false, defaultValue = "25") Integer maxResults,
                                                     @RequestParam(required = false, defaultValue = "false") Boolean filter,
                                                     @RequestParam(required = false, defaultValue = "false") Boolean trashed) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final List<String> topicList = new LinkedList<>();
        if (topics != null) {
            topicList.addAll(Arrays.asList(topics.split(",")));
        }
        final List<SecureToSend> posts = postManger.get(topicList, firstResult, maxResults, filter, trashed)
                .stream().map(SecurePost::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(universalResponse.setData(posts), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/post/{postId}/trashed", method = RequestMethod.PATCH, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> setTrashed(@PathVariable Integer postId,
                                                        @RequestParam(required = true) Boolean trashed,
                                                        Principal principal) {
        postManger.updateTrashed(postId, trashed, principal.getName());
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }

    private static class PostAndTransaction {
        @Null
        @Valid
        private Post post;

        @Null
        @Valid
        private PostCreation postCreation;

        public PostCreation getPostCreation() {
            return postCreation;
        }

        public void setPostCreation(PostCreation postCreation) {
            this.postCreation = postCreation;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }
    }
}
