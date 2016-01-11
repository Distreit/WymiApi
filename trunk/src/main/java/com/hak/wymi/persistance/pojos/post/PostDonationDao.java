package com.hak.wymi.persistance.pojos.post;

import java.util.List;

public interface PostDonationDao {
    void save(PostDonation postDonation);

    List<PostDonation> getUnprocessed();

    List<PostDonation> get(String topicName);

    List<PostDonation> getForUser(String userName, Integer firstResult, Integer maxResults);
}
