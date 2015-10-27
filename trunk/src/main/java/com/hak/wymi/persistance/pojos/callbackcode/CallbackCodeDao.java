package com.hak.wymi.persistance.pojos.callbackcode;

public interface CallbackCodeDao {
    void save(CallbackCode callbackCode);

    CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);

    void delete(CallbackCode callbackCode);

    CallbackCode getFromCode(String code, CallbackCodeType type);
}
