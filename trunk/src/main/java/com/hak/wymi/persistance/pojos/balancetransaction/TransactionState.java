package com.hak.wymi.persistance.pojos.balancetransaction;

public enum TransactionState {
    /**
     * The transaction is new and has not been processed yet. Can easily be cancelled.
     */
    UNPROCESSED,

    /**
     * The transaction has been processed, points have been moved around. Only special cases can be cancelled now.
     * Example bids.
     */
    PROCESSED,

    /**
     * The transaction is canceled, points have been returned to their original owner.
     */
    CANCELED
}
