package com.hak.wymi.persistance.pojos.comment;

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
@Table(name = "commentdonation")
public class CommentDonation extends AbstractBalanceTransaction implements DonationTransaction {
    private static final long serialVersionUID = -6257413713811479971L;

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
    public HasPointsBalance getDestination() {
        return this.comment.getAuthor().getBalance();
    }

    @Override
    public User getDestinationUser() {
        return this.comment.getAuthor();
    }

    @Override
    public HasPointsBalance getTarget() {
        return this.comment;
    }

    public Integer getCommentDonationId() {
        return this.commentDonationId;
    }

    @Override
    public boolean paySiteTax() {
        return true;
    }
}
