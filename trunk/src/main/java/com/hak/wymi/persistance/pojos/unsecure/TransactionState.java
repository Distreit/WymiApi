package com.hak.wymi.persistance.pojos.unsecure;

public enum TransactionState {
    UNPROCESSED {
        @Override
        public String toString() {
            return "UNPROCESSED";
        }
    }, PROCESSED {
        @Override
        public String toString() {
            return "PROCESSED";
        }
    }, CANCELED {
        @Override
        public String toString() {
            return "CANCELED";
        }
    };

    @Override
    public abstract String toString();
}
