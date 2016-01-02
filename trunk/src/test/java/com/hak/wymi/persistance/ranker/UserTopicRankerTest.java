package com.hak.wymi.persistance.ranker;

import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class UserTopicRankerTest extends TestCase {
    private Topic topic;
    private UserTopicRanker ranker;
    private List<DonationTransaction> donations;
    private List<User> users;

    @Before
    public void setUp() throws Exception {
        topic = new Topic();
        ranker = new UserTopicRanker(topic);
        donations = new LinkedList<>();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRunOn() throws Exception {
//        CommentDonation commentDonationA = new CommentDonation();
//        commentDonationA.setAmount(10);
//
//        donations.add(commentDonationA);
//
//        ranker.runOn(donations, 0.0000001, 1000, 0.825);
//        for (UserTopicRank rank : ranker.getUserRanks()) {
//            System.out.println(rank.getRank());
//        }
    }
}