package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.interfaces.HasPassword;
import com.hak.wymi.validations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, HasPassword> {
    @Override
    public void initialize(PasswordsMatch passwordsMatch) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(HasPassword hasPasswordObject, ConstraintValidatorContext cxt) {
        return hasPasswordObject.passwordsMatch();
    }
}