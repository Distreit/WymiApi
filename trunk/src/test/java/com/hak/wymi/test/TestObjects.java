package com.hak.wymi.test;

import com.hak.wymi.persistance.pojos.comment.Comment;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.post.PostDonation;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.Balance;
import com.hak.wymi.persistance.pojos.user.User;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TestObjects {
    List<User> users = new LinkedList<>();
    List<Topic> topics = new LinkedList<>();
    List<Post> posts = new LinkedList<>();
    List<Comment> comments = new LinkedList<>();
    List<CommentDonation> commentDonations = new LinkedList<>();
    List<PostDonation> postDonation = new LinkedList<>();

    private int userCount = 0;
    private int topicCount = 0;

    public TestObjects() {
        createUsers(10);
        createTopics(10);

        for (Topic topic : topics) {
            posts.addAll(users.stream().map(user -> createPost(topic, user)).collect(Collectors.toList()));
        }
    }

    private Post createPost(Topic topic, User user) {
        Post post = new Post();
        post.setDeleted(false);
        post.setPoints(0);
        post.setText("Some text");
        post.setTrashed(false);
        post.setUser(user);
        post.setUpdated(new DateTime());
        post.setCommentCounts(0);
        post.setIsText(true);
        post.setScore(0.0);
        post.setBase(new DateTime().getMillis() / 1.0);
        post.setDonations(0);
        post.setCreated(new DateTime());
        post.setTopic(topic);

        return post;
    }

    private void createTopics(int count) {
        for (int i = 0; i < count; i += 1) {
            Topic topic = createTopic(users.get(i));
            topics.add(topic);
        }
    }

    private void createUsers(int count) {
        for (int i = 0; i < count; i += 1) {
            User user = createUser();
            users.add(user);
        }
    }

    private Topic createTopic(User owner) {
        Topic topic = new Topic();

        topic.setDescription("topic" + topicCount + " description.");
        topic.setRentDueDate(new DateTime().plusDays(1));
        topic.setRent(topicCount);
        topic.setCreated(new DateTime());
        topic.setVersion(0);
        topic.setFeePercent(topicCount);
        topic.setFeeFlat(topicCount);
        topic.setFilterCount(0);
        topic.setName("topic" + topicCount);
        topic.setOwner(owner);
        topic.setSubscriberCount(0);
        topic.setSubscribers(new LinkedHashSet<>());
        topic.setTitle("topic" + topicCount + " title.");
        topic.setTopicId(topicCount);
        topic.setUpdated(new DateTime());

        return topic;
    }

    private User createUser() {
        String userName = "user" + userCount;
        User user = new User();
        user.setLastJurored(null);
        user.setPassword("password" + userName);
        user.setBalance(new Balance());
        user.setConfirmEmail(null);
        user.setConfirmPassword(null);
        user.setEmail(String.format("%1$s@%1$s.com", userName));
        user.setFilters(new HashSet<>());
        user.setName(userName);
        user.setRoles("ROLE_USER,ROLE_VALIDATED");
        user.setSubscriptions(new HashSet<>());
        user.setUserId(userCount);
        user.setValidated(true);
        user.setWillingJuror(true);
        user.setCreated(new DateTime());
        user.setUpdated(new DateTime());
        user.setVersion(0);

        userCount += 1;
        return user;
    }
}