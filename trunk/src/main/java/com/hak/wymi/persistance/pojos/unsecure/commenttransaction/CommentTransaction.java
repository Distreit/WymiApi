package com.hak.wymi.persistance.pojos.unsecure.commenttransaction;

import com.hak.wymi.persistance.pojos.unsecure.comment.Comment;
import com.hak.wymi.persistance.pojos.unsecure.transactions.BalanceTransaction;
import com.hak.wymi.persistance.pojos.unsecure.transactions.TransactionState;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import javax.validation.groups.Default;
import java.util.Date;

@Entity
@Table(name = "commentTransaction")
public class CommentTransaction extends BalanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer commentTransactionId;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;

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

    public Integer getCommentTransactionId() {
        return commentTransactionId;
    }

    public void setCommentTransactionId(Integer commentTransactionId) {
        this.commentTransactionId = commentTransactionId;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public TransactionState getState() {
        return this.state;
    }
}
