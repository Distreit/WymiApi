package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CallbackCodeManager {
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
}
