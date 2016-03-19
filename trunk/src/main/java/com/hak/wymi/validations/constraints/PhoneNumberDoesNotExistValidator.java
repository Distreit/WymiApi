package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.PhoneNumberDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class PhoneNumberDoesNotExistValidator implements ConstraintValidator<PhoneNumberDoesNotExist, Object> {
    @Autowired
    private UserManager userManager;

    @Override
    public void initialize(PhoneNumberDoesNotExist emailDoesNotExist) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object != null) {
            String phoneNumber = null;
            if (object instanceof User) {
                phoneNumber = ((User) object).getPhoneNumber();
            } else if (object instanceof String) {
                phoneNumber = (String) object;
            }

            if (phoneNumber != null && !"".equals(phoneNumber)) {
                return !userManager.phoneNumberExists(phoneNumber);
            }

            if (phoneNumber == null) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }
}