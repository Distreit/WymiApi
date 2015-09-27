package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.GenericBalanceTransaction;
import com.hak.wymi.persistance.pojos.user.User;
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
@Table(name = "postCreation")
public class PostCreation extends GenericBalanceTransaction {
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
    public Integer getSourceUserId() {
        return this.post.getUser().getUserId();
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
    public User getSourceUser() {
        return this.post.getUser();
    }

    @Override
    public String getTargetUrl() {
        // TODO: CREATE URL
        return "http://localhost/home";
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
    public boolean paySiteTax() {
        return true;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }
}
