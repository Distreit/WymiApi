package com.hak.wymi.persistance.pojos.ownershiptransaction;

public enum OwnershipTransactionState {
    /**
     * Rent date was processed and transaction is in holding period waiting for current owner to have a chance to
     * outbid.
     * <p/>
     * If transaction exists, is in waiting state and the date is past the waiting period the transaction can be
     * processed;
     */
    WAITING,

    /**
     * Waiting period has expired and bid has been processed.
     */
    PROCESSED
}
