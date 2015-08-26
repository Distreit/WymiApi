package com.hak.wymi.utility;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;

public class DaoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoHelper.class);

    private DaoHelper() {
    }

    public static interface TransactionWrapper {
        void execute(Session session) throws ValidationException;
    }

    public static boolean genericTransaction(Session session, TransactionWrapper tw) {
        Transaction tx = session.beginTransaction();
        try {
            tw.execute(session);
            tx.commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
}
