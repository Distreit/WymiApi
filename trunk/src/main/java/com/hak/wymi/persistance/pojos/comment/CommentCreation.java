package com.hak.wymi.persistance.pojos.comment;

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
@Table(name = "commentcreation")
public class CommentCreation extends AbstractBalanceTransaction {
    private static final long serialVersionUID = 1538010638630429551L;

    @Id
    private Integer commentId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @Null(groups = Creation.class)
    private Comment comment;

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
    public HasPointsBalance getSource() {
        return this.comment.getAuthor().getBalance();
    }

    @Override
    public HasPointsBalance getDestination() {
        return this.comment.getPost().getTopic().getOwner().getBalance();
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public String getTargetUrl() {
        return this.comment.getUrl();
    }

    @Override
    public Integer getTransactionId() {
        return this.commentId;
    }

    @Override
    public Object getDependent() {
        return this.comment;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Integer feePercent) {
        this.feePercent = feePercent;
    }

    @Override
    public Integer getTaxerUserId() {
        return comment.getPost().getTopic().getOwner().getUserId();
    }

    @Override
    public Integer getTaxRate() {
        return 0;
    }

    @Override
    public boolean isUniqueToUser() {
        // Doesn't need to track uniqueness.
        return false;
    }

    @Override
    public boolean shouldPaySiteTax() {
        return true;
    }

    @Override
    public Message getCancellationMessage() {
        final String messageText;
        if (comment.getParentComment() == null) {
            messageText = String
                    .format("Your comment to the post %s has been cancelled.%n%n\"%s\"",
                            comment.getPost().getHref(), comment.getContent());
        } else {
            messageText = String
                    .format("Your reply to the comment %s for the post %s has been cancelled.%n%n\"%s\"",
                            comment.getUrl(), comment.getPost().getHref(), comment.getContent());
        }
        return new Message(this.comment.getAuthor(), null, "Comment creation failure", messageText);
    }

    public Integer getFeeFlat() {
        return feeFlat;
    }

    public void setFeeFlat(Integer feeFlat) {
        this.feeFlat = feeFlat;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }
}
