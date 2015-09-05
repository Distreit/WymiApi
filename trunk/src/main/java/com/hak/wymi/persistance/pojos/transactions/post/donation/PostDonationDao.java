package com.hak.wymi.persistance.pojos.transactions.post.donation;

import java.util.List;

public interface PostDonationDao {
    boolean save(PostDonation postDonation);

    List<PostDonation> getUnprocessed();
}
