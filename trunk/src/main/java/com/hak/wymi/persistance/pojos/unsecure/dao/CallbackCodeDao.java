package com.hak.wymi.persistance.pojos.unsecure.dao;

import com.hak.wymi.persistance.pojos.unsecure.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.CallbackCodeType;

public interface CallbackCodeDao {
    boolean save(CallbackCode callbackCode);

    CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);

    boolean delete(CallbackCode callbackCode);

    CallbackCode getFromCode(String code, CallbackCodeType type);
}
