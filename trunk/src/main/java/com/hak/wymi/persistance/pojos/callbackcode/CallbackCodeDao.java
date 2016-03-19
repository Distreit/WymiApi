package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.user.User;

import java.util.List;

public interface CallbackCodeDao {
    void save(CallbackCode callbackCode);

    CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);

    void delete(CallbackCode callbackCode);

    void delete(User user, CallbackCodeType type);

    CallbackCode getFromCode(String code, CallbackCodeType type);

    CallbackCode getFromCode(String code);

    void cleanUp();

    List<CallbackCode> getCodesForUser(String name, CallbackCodeType phoneNumberVerification);
}
