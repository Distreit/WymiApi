package com.hak.wymi.utility;

import com.hak.wymi.persistance.pojos.PersistentObject;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.joda.time.DateTime;

import java.io.Serializable;

public class EntityInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = -6271236007196299795L;

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] state,
                                Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof PersistentObject) {
            int indexOf = ArrayUtils.indexOf(propertyNames, "updated");
            state[indexOf] = new DateTime();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        if (entity instanceof PersistentObject) {
            int indexOf = ArrayUtils.indexOf(propertyNames, "created");
            state[indexOf] = new DateTime();

            indexOf = ArrayUtils.indexOf(propertyNames, "updated");
            state[indexOf] = new DateTime();
            return true;
        }
        return false;
    }
}
