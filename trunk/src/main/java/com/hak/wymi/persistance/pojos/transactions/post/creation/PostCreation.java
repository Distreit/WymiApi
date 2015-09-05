package com.hak.wymi.persistance.pojos.transactions.post.creation;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "postCreation")
public class PostCreation implements BalanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer postCreationId;

    @ManyToOne
    @JoinColumn(name = "postId")
    @Null(groups = Creation.class)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "authorId")
    @Null(groups = Creation.class)
    private User author;

    @Min(value = 0)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer feeFlat;

    @Min(value = 0)
    @Max(value = 100)
    @NotNull(groups = {Default.class, Creation.class})
    private Integer feePercent;

    @Enumerated(EnumType.STRING)
    @Null(groups = Creation.class)
    private TransactionState state;

    @Version
    @Null(groups = Creation.class)
    private Integer version;

    @Null(groups = Creation.class)
    private Date updated;

    @Null(groups = Creation.class)
    private Date created;

    @Override
    public TransactionState getState() {
        return this.state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Integer getAmount() {
        return this.feeFlat;
    }

    @Override
    public Integer getSourceUserId() {
        return this.author.getUserId();
    }

    @Override
    public Integer getDestinationUserId() {
        return this.post.getTopic().getOwner().getUserId();
    }

    @Override
    public Integer getTargetId() {
        return this.post.getTopic().getOwner().getUserId();
    }

    @Override
    public Class getTargetClass() {
        return this.post.getTopic().getOwner().getClass();
    }

    @Override
    public User getSourceUser() {
        return this.author;
    }

    @Override
    public String getTargetUrl() {
        // TODO: CREATE URL
        return "http://localhost/wymi/home";
    }

    @Override
    public User getDestinationUser() {
        return this.post.getUser();
    }

    @Override
    public Integer getTransactionId() {
        return this.postCreationId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Integer feePercent) {
        this.feePercent = feePercent;
    }

    public void setAuthor(User author) {
        this.author = author;
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
}
