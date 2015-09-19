package com.hak.wymi.persistance.pojos.comment;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.balancetransaction.TransactionState;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.Formula;

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
@Table(name = "commentdonation")
public class CommentDonation implements BalanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer commentDonationId;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;

    @Formula("(select count(d.commentId) from commentdonation d where d.commentId=commentId and d.sourceUserId=sourceUserId and d.state='PROCESSED')")
    private Integer userDonationCount;

    @Min(value = 0, groups = {Default.class, Creation.class})
    private Integer amount;

    @Version
    private Integer version;

    private Date updated;

    private Date created;

    @Enumerated(EnumType.STRING)
    private TransactionState state;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public User getSourceUser() {
        return sourceUser;
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
        return this.getCommentDonationId();
    }

    @Override
    public Object getDependent() {
        return null;
    }

    @Override
    public Integer getTaxerUserId() {
        return this.comment.getPost().getTopic().getOwner().getUserId();
    }

    @Override
    public Integer getTaxRate() {
        return comment.getTaxRate();
    }

    @Override
    public boolean isUniqueToUser() {
        return userDonationCount == 0;
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
    public Integer getDestinationId() {
        return this.comment.getAuthorId();
    }

    @Override
    public Class getDestinationClass() {
        return this.comment.getAuthor().getClass();
    }

    @Override
    public HasPointsBalance getDestinationObject() {
        return this.comment;
    }

    @Override
    public Integer getTargetId() {
        return this.comment.getCommentId();
    }

    @Override
    public Class getTargetClass() {
        return this.comment.getClass();
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

    @Override
    public TransactionState getState() {
        return this.state;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getCommentDonationId() {
        return this.commentDonationId;
    }

    @Override
    public boolean paySiteTax() {
        return true;
    }
}
