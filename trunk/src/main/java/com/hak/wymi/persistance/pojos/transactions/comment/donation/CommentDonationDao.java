package com.hak.wymi.persistance.pojos.transactions.comment.donation;

import java.util.List;

public interface CommentDonationDao {
    boolean save(CommentDonation commentDonation);

    List<CommentDonation> getUnprocessed();
}
