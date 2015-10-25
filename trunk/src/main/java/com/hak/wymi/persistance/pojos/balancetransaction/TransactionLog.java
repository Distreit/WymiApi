package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Null;

@Entity
@Table(name = "transactionlog")
public class TransactionLog extends PersistentObject {
    private static final long serialVersionUID = 7506282118915273476L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class})
    private Integer transactionLogId;

    private Integer transactionId;

    private Class transactionClass;

    private Integer amountPayed;

    private Integer destinationReceived;

    private Integer taxerReceived;

    private Integer siteReceived;

    private Integer targetReceived;

    private Boolean canceled = Boolean.FALSE;

    public TransactionLog() {
        super();
    }

    public TransactionLog(BalanceTransaction transaction) {
        super();
        this.transactionClass = transaction.getClass();
        this.transactionId = transaction.getTransactionId();
    }

    public Integer getTransactionLogId() {
        return transactionLogId;
    }

    public void setTransactionLogId(Integer transactionLogId) {
        this.transactionLogId = transactionLogId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Class getTransactionClass() {
        return transactionClass;
    }

    public void setTransactionClass(Class transactionClass) {
        this.transactionClass = transactionClass;
    }

    public Integer getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(Integer amountPayed) {
        this.amountPayed = amountPayed;
    }

    public Integer getDestinationReceived() {
        return destinationReceived;
    }

    public void setDestinationReceived(Integer destinationReceived) {
        this.destinationReceived = destinationReceived;
    }

    public Integer getTaxerReceived() {
        return taxerReceived;
    }

    public void setTaxerReceived(Integer taxerReceived) {
        this.taxerReceived = taxerReceived;
    }

    public Integer getSiteReceived() {
        return siteReceived;
    }

    public void setSiteReceived(Integer siteReceived) {
        this.siteReceived = siteReceived;
    }

    public Integer getTargetReceived() {
        return targetReceived;
    }

    public void setTargetReceived(Integer targetReceived) {
        this.targetReceived = targetReceived;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }
}
