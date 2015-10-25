package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.message.Message;
import org.joda.time.DateTime;

public interface BalanceTransaction {
    TransactionState getState();

    void setState(TransactionState state);

    /**
     * @return The timestamp when the transaction was created.
     */
    DateTime getCreated();

    /**
     * @return The amount the transaction is for.
     */
    Integer getAmount();

    /**
     * @return The source which initiated the transaction (The paying user).
     */
    HasPointsBalance getSource();

    /**
     * @return The actual object that will be receiving the points after being taxed.
     */
    HasPointsBalance getDestination();

    /**
     * @return The the object that will be points as a score increase or something like that... NOT actual points.
     * Example: if donating to a comment the author would receive actual points but the target (the comment) would just
     * get a score increase.
     */
    HasPointsBalance getTarget();

    /**
     * @return The url of the target. Used in things like a cancel message.
     */
    String getTargetUrl();

    /**
     * @return The transactions id.
     */
    Integer getTransactionId();

    /**
     * @return The dependent object which should be removed if the transaction fails.
     */
    Object getDependent();


    /**
     * @return The id of the user that will be receiving tax revenue. For comment and post donations this would be the
     * topic owner. For comment and post creations wouldn't exist.
     */
    Integer getTaxerUserId();


    /**
     * @return The tax rate of transactions that pay a % fee.
     */
    Integer getTaxRate();

    /**
     * @return whether the transaction is unique to a user. In the case of donations this be true the first time a user
     * donated to a specific post of comment. For transactions like creation it would always be true.
     */
    boolean isUniqueToUser();

    /**
     * @return whether the transaction should pay a site tax. Would be true in all cases except for TopicBids so far.
     */
    boolean shouldPaySiteTax();

    TransactionLog getTransactionLog();

    void setTransactionLog(TransactionLog transactionLog);

    /**
     * @return Message to be sent on cancellation of the transaction.
     */
    Message getCancellationMessage();

    SecureBalanceTransaction getSecureBalanceTransaction();
}
