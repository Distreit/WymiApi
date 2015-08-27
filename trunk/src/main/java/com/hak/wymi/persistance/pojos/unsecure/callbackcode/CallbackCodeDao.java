package com.hak.wymi.persistance.pojos.unsecure.callbackcode;

public interface CallbackCodeDao {
    boolean save(CallbackCode callbackCode);

    CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);

    boolean delete(CallbackCode callbackCode);

    CallbackCode getFromCode(String code, CallbackCodeType type);
}
