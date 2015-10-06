package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.post.PostDonation;
import com.hak.wymi.persistance.pojos.post.PostDonationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostDonationManager {
    @Autowired
    private PostDonationDao postDonationDao;

    @Transactional
    public void save(PostDonation postDonation) {
        postDonationDao.save(postDonation);
    }

    @Transactional
    public List<PostDonation> get(String topicName) {
        return postDonationDao.get(topicName);
    }

    @Transactional
    public List<PostDonation> getUnprocessed() {
        return postDonationDao.getUnprocessed();
    }
}
