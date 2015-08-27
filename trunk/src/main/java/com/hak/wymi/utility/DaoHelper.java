package com.hak.wymi.utility;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;

public final class DaoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoHelper.class);

    private DaoHelper() {
    }

    public static boolean genericTransaction(Session session, TransactionWrapper transactionWrapper) {
        final Transaction transaction = session.beginTransaction();
        try {
            if (transactionWrapper.execute(session)) {
                transaction.commit();
                return true;
            } else if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
        return false;
    }

    @FunctionalInterface
    public static interface TransactionWrapper {
        boolean execute(Session session) throws ValidationException;
    }
}
