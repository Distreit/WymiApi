package com.hak.wymi.persistance.pojos.comment;

import java.util.List;

public interface CommentDonationDao {
    boolean save(CommentDonation commentDonation);

    List<CommentDonation> getUnprocessed();

    List<CommentDonation> get(String topicName);
}
