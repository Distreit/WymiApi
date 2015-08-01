package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.user.User;

public interface CallbackCodeDao {
    public boolean save(CallbackCode callbackCode);

    public CallbackCode get(User user, CallbackCodeType type);

    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType type);
}
