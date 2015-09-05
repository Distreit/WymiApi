package com.hak.wymi.persistance.pojos.transactions.post.donation;

import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.persistance.pojos.transactions.TransactionState;
import com.hak.wymi.persistance.pojos.transactions.balance.BalanceTransaction;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "postdonation")
public class PostDonation implements BalanceTransaction {
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

    @Version
    private Integer version;

    private Date updated;

    private Date created;

    @Enumerated(EnumType.STRING)
    private TransactionState state;

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
        return "http://localhost/wymi/home";
    }

    @Override
    public User getDestinationUser() {
        return this.getPost().getUser();
    }

    @Override
    public Integer getTransactionId() {
        return this.getPostDonationId();
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
    public Integer getDestinationUserId() {
        return this.post.getUser().getUserId();
    }

    @Override
    public Integer getTargetId() {
        return this.post.getPostId();
    }

    @Override
    public Class getTargetClass() {
        return this.post.getClass();
    }

    public Date getUpdated() {
        return (Date) updated.clone();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Date getCreated() {
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public TransactionState getState() {
        return state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }
}
