package com.hak.wymi.utility;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoHelper.class);

    private DaoHelper() {
    }

    public static boolean simpleDelete(Object o, Session session) {
        Transaction tx = session.beginTransaction();
        try {
            session.delete(o);
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

    public static boolean simpleSaveOrUpdate(Object o, Session session, boolean save) {
        Transaction tx = session.beginTransaction();
        try {
            if (save) {
                session.persist(o);
            } else {
                session.update(o);
            }
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
