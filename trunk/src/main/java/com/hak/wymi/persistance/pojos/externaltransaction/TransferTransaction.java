package com.hak.wymi.persistance.pojos.externaltransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.balancetransaction.AbstractBalanceTransaction;
import com.hak.wymi.persistance.pojos.message.Message;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;

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
@Table(name = "transfertransaction")
public class TransferTransaction extends AbstractBalanceTransaction {
    private static final long serialVersionUID = -7991845080838000302L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer transferTransactionId;

    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;

    @ManyToOne
    @JoinColumn(name = "destinationUserId")
    private User destinationUser;

    @Min(value = 0, groups = {Default.class, Creation.class})
    private Integer amount;

    @Override
    public Integer getAmount() {
        return amount;
    }

    @Override
    public HasPointsBalance getSource() {
        return sourceUser.getBalance();
    }

    @Override
    public HasPointsBalance getDestination() {
        return destinationUser.getBalance();
    }

    @Override
    public HasPointsBalance getTarget() {
        return null;
    }

    @Override
    public String getTargetUrl() {
        return null;
    }

    @Override
    public Integer getTransactionId() {
        return transferTransactionId;
    }

    @Override
    public Object getDependent() {
        return null;
    }

    @Override
    public Integer getTaxerUserId() {
        return null;
    }

    @Override
    public Integer getTaxRate() {
        return 0;
    }

    @Override
    public boolean isUniqueToUser() {
        return false;
    }

    @Override
    public boolean shouldPaySiteTax() {
        return false;
    }

    @Override
    public Message getCancellationMessage() {
        final String messageText = String.format("Point transfer to %s for %d cancelled.", destinationUser.getName(), amount);
        return new Message(this.sourceUser, null, "Point transfer cancelled", messageText);
    }

    public void setSource(User source) {
        this.sourceUser = source;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setDestination(User destination) {
        this.destinationUser = destination;
    }
}
