package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.comment.CommentDonationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentDonationManager {
    @Autowired
    private CommentDonationDao commentDonationDao;

    @Transactional
    public void save(CommentDonation commentDonation) {
        commentDonationDao.save(commentDonation);
    }

    @Transactional
    public List<CommentDonation> get(String topicName) {
        return commentDonationDao.get(topicName);
    }

    @Transactional
    public List<CommentDonation> getUnprocessed() {
        return commentDonationDao.getUnprocessed();
    }
}
