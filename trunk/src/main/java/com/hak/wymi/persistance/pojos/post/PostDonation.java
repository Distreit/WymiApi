package com.hak.wymi.persistance.pojos.post;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.AbstractBalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.DonationTransaction;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;

@Entity
@Table(name = "postdonation")
public class PostDonation extends AbstractBalanceTransaction implements DonationTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer postDonationId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;

    @Min(value = 0, groups = {Default.class, Creation.class})
    private Integer amount;

    @Formula("(select count(d.postId) from postdonation d where d.postId=postId and d.sourceUserId=sourceUserId and d.state='PROCESSED')")
    private Integer userDonationCount;

    public Integer getPostDonationId() {
        return this.postDonationId;
    }

    public void setPostDonationId(Integer postTransactionId) {
        this.postDonationId = postTransactionId;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public User getSourceUser() {
        return this.sourceUser;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    @Override
    public String getTargetUrl() {
        // TODO: CREATE URL
        return "http://localhost/home";
    }

    @Override
    public Integer getTransactionId() {
        return this.getPostDonationId();
    }

    @Override
    public Object getDependent() {
        return null;
    }

    @Override
    public Integer getTaxerUserId() {
        return this.post.getTopic().getOwner().getUserId();
    }

    @Override
    public Integer getTaxRate() {
        return this.post.getTaxRate();
    }

    @Override
    public boolean isUniqueToUser() {
        return this.userDonationCount == 0;
    }

    @Override
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public Integer getSourceUserId() {
        return this.sourceUser.getUserId();
    }

    @Override
    public HasPointsBalance getDestination() {
        return this.post.getUser().getBalance();
    }

    @Override
    public User getDestinationUser() {
        return this.post.getUser();
    }

    @Override
    public HasPointsBalance getTarget() {
        return this.post;
    }

    public void setUserDonationCount(Integer userDonationCount) {
        this.userDonationCount = userDonationCount;
    }

    @Override
    public boolean paySiteTax() {
        return true;
    }
}
