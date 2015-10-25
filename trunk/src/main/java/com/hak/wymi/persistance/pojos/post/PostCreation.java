package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.AbstractBalanceTransaction;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;

@Entity
@Table(name = "postcreation")
public class PostCreation extends AbstractBalanceTransaction {
    private static final long serialVersionUID = -6435436692312120281L;

    @Id
    private Integer postId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @Null(groups = Creation.class)
    private Post post;

    @Min(value = 0)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer feeFlat;

    @Min(value = 0)
    @Max(value = 100)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer feePercent;

    @Override
    public Integer getAmount() {
        return this.feeFlat;
    }

    @Override
    public HasPointsBalance getDestination() {
        return this.post.getTopic().getOwner().getBalance();
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public HasPointsBalance getSource() {
        return this.post.getUser().getBalance();
    }

    @Override
    public String getTargetUrl() {
        return this.post.getUrl();
    }

    @Override
    public Integer getTransactionId() {
        return this.postId;
    }

    @Override
    public Object getDependent() {
        return this.post;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Integer feePercent) {
        this.feePercent = feePercent;
    }

    @Override
    public Integer getTaxerUserId() {
        return this.post.getTopic().getOwner().getUserId();
    }

    @Override
    public Integer getTaxRate() {
        return 0;
    }

    @Override
    public boolean isUniqueToUser() {
        // Post creations don't need to track uniqueness.
        return false;
    }

    public Integer getFeeFlat() {
        return feeFlat;
    }

    public void setFeeFlat(Integer feeFlat) {
        this.feeFlat = feeFlat;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean shouldPaySiteTax() {
        return true;
    }

    @Override
    public Message getCancellationMessage() {
        final String postContent;
        if (post.getIsText()) {
            postContent = post.getText();
        } else {
            postContent = post.getHref();
        }

        final String messageText = String.format("Your post to the topic %s failed and was cancelled.%n%n\"%s%n%s\"",
                post.getTopic().getUrl(), post.getTitle(), postContent);
        return new Message(this.getPost().getUser(), null, "Comment creation failure", messageText);
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }
}
