package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.smsmessage.SMSMessage;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class CallbackCodeManager {
    private static final int BITS = 130;

    private static final int RADIX = 36;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private CallbackCodeDao callbackCodeDao;

    @Autowired
    private SMSMessageManager smsMessageManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

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
        callbackCode.setCode(getRandomString(25));
        callbackCode.setType(type);
        callbackCodeDao.save(callbackCode);
        return callbackCode.getCode();
    }

    @Transactional
    public String processCode(String code) throws InvalidValueException {
        callbackCodeDao.cleanUp();
        final CallbackCode callbackCode = callbackCodeDao.getFromCode(code);
        if (callbackCode != null) {
            final User user = callbackCode.getUser();
            switch (callbackCode.getType()) {
                case VALIDATION:
                    // TODO: Update account validation to be handled here.
                    return "VALIDATION";
                case EMAIL_CHANGE:
                    user.setEmail(user.getNewEmail());
                    user.setNewEmail(null);
                    userDao.update(user);
                    callbackCodeDao.delete(callbackCode);
                    return "EMAIL_CHANGE";
                case PASSWORD_RESET:
                    // TODO: Update password reset to be handled here.
                    return "PASSWORD_RESET";
                case PHONE_NUMBER_VERIFICATION:
                    user.setReceivedFunds(true);
                    userDao.update(user);
                    transactionProcessor.createPointsFor(user, 1000);
                    callbackCodeDao.delete(callbackCode);
                    return "PHONE_NUMBER_VERIFICATION";
                default:
                    return null;
            }
        }
        return null;
    }

    public String getRandomString(int length) {
        final StringBuilder buffer = new StringBuilder(getRandomString());
        while (buffer.length() < length) {
            buffer.append(getRandomString());
        }

        return buffer.substring(0, length);
    }

    public String getRandomString() {
        return (new BigInteger(BITS, secureRandom)).toString(RADIX).replaceAll("[0o1il]", "");
    }

    @Transactional
    public void generate(String type, Principal principal) {
        switch (type) {
            case "PHONE_NUMBER_VERIFICATION":
                generatePhoneNumberVerification(principal);
                break;
            default:
                throw new UnsupportedOperationException("Code type not recognized.");
        }
    }

    private void generatePhoneNumberVerification(Principal principal) {
        final User user = userDao.getFromName(principal.getName());
        List<CallbackCode> codes = callbackCodeDao.getCodesForUser(principal.getName(), CallbackCodeType.PHONE_NUMBER_VERIFICATION);

        codes.forEach(callbackCodeDao::delete);

        CallbackCode callbackCode = new CallbackCode(user, CallbackCodeType.PHONE_NUMBER_VERIFICATION, getRandomString(5));
        callbackCodeDao.save(callbackCode);

        SMSMessage message = new SMSMessage(user.getPhoneNumber(), "Your WhereYourMouthIs phone number verification code is: " + callbackCode.getCode().toUpperCase());
        smsMessageManager.save(message);
    }
}
