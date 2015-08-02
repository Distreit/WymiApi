package com.hak.wymi.persistance.pojos.callbackcode;

public interface CallbackCodeDao {
    public boolean save(CallbackCode callbackCode);

    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);

    public boolean delete(CallbackCode callbackCode);

    public CallbackCode getFromCode(String code, CallbackCodeType type);
}
