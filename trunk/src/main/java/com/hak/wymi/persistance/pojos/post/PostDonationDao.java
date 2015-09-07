package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostDonationDao {
    boolean save(PostDonation postDonation);

    List<PostDonation> getUnprocessed();
}
