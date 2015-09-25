package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.interfaces.SecureToSend;

public class SecureCommentDonation implements SecureToSend {

    private final Integer amount;
    private final String sourceUserName;
    private final String destinationUserName;

    public SecureCommentDonation(CommentDonation commentDonation) {
        this.amount = commentDonation.getAmount();
        this.sourceUserName = commentDonation.getSourceUser().getName();
        this.destinationUserName = commentDonation.getComment().getAuthor().getName();
    }

    public String getDestinationUserName() {
        return destinationUserName;
    }

    public String getSourceUserName() {
        return sourceUserName;
    }

    public Integer getAmount() {
        return amount;
    }
}
