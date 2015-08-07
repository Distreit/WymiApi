package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeDao;
import com.hak.wymi.validations.CallbackCodeExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class CallbackCodeExistsValidator implements ConstraintValidator<CallbackCodeExists, String> {
    @Autowired
    private CallbackCodeDao callbackCodeDao;
    private CallbackCodeExists callbackCodeExists;

    @Override
    public void initialize(CallbackCodeExists callbackCodeExists) {
        this.callbackCodeExists = callbackCodeExists;
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext cxt) {
        if (code != null) {
            CallbackCode callbackCode = callbackCodeDao.getFromCode(code, callbackCodeExists.type());
            if (callbackCode != null) {
                return true;
            }
        }

        return false;
    }
}