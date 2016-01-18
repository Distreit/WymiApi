package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class CallbackCodeManager {
    // I think, but I'm not sure, this is the number of characters that the output will have.
    private static final int NUMBER_OF_CHARACTERS = 130;
    // Not sure about this either. Mostly magic.
    private static final int RADIX = 32;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private CallbackCodeDao callbackCodeDao;

    @Transactional
    public CallbackCode getFromCode(String code, CallbackCodeType passwordReset) {
        return callbackCodeDao.getFromCode(code, passwordReset);
    }

    @Transactional
    public void delete(CallbackCode callbackCode) {
        callbackCodeDao.delete(callbackCode);
    }

    @Transactional
    public void save(CallbackCode callbackCode) {
        callbackCodeDao.save(callbackCode);
    }

    @Transactional
    public CallbackCode getFromUserName(String userName, String code, CallbackCodeType validation) {
        return callbackCodeDao.getFromUserName(userName, code, validation);
    }

    @Transactional
    public String saveNew(User user, CallbackCodeType type) {
        callbackCodeDao.delete(user, type);
        final CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(NUMBER_OF_CHARACTERS, secureRandom)).toString(RADIX));
        callbackCode.setType(type);
        callbackCodeDao.save(callbackCode);
        return callbackCode.getCode();
    }

    @Transactional
    public String processCode(String code) {
        callbackCodeDao.cleanUp();
        final CallbackCode callbackCode = callbackCodeDao.getFromCode(code);
        if (callbackCode != null) {
            switch (callbackCode.getType()) {
                case VALIDATION:
                    // TODO: Update account validation to be handled here.
                    return "VALIDATION";
                case EMAIL_CHANGE:
                    final User user = callbackCode.getUser();
                    user.setEmail(user.getNewEmail());
                    user.setNewEmail(null);
                    userDao.update(user);
                    callbackCodeDao.delete(callbackCode);
                    return "EMAIL_CHANGE";
                case PASSWORD_RESET:

                    // TODO: Update password reset to be handled here.
                    return "PASSWORD_RESET";
                default:
                    return null;
            }
        }
        return null;
    }
}
