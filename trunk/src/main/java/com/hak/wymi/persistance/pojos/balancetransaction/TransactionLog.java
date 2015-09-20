package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Null;
import java.util.Date;

@Entity
@Table(name = "transactionLog")
public class TransactionLog {

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

    private Boolean canceled = false;

    @Version
    private Integer version;

    private Date updated;

    private Date created;
    @OneToOne(mappedBy = "transactionLog", optional = false)
    private CommentCreation transactionLog;

    public TransactionLog(BalanceTransaction transaction) {
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public CommentCreation getTransactionLog() {
        return transactionLog;
    }

    public void setTransactionLog(CommentCreation transactionLog) {
        this.transactionLog = transactionLog;
    }
}
