package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.validations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
    @Override
    public void initialize(PasswordsMatch passwordsMatch) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object instanceof User) {
            final User user = (User) object;
            return user.getPassword().equals(user.getConfirmPassword());
        } else if (object instanceof PasswordChange) {
            final PasswordChange passwordChange = (PasswordChange) object;
            return passwordChange.getPassword().equals(passwordChange.getConfirmPassword());
        } else {
            return false;
        }
    }
}