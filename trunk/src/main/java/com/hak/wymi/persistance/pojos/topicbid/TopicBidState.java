package com.hak.wymi.persistance.pojos.topicbid;

public enum TopicBidState {
    /**
     * The bid has been created but the rent due date has not been passed. Can still be canceled.
     */
    WAITING,

    /**
     * The bid has been accepted as the winning bid. The bidder will get control of the topic unless the current owner
     * outbids during the waiting period.
     */
    ACCEPTED,

    /**
     * The waiting period has expired and the bids balance is going to be dispersed.
     */
    PROCESSED
}
