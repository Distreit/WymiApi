package com.hak.wymi.validations.constraints;

import com.hak.wymi.utility.passwordstrength.PasswordStrengthChecker;
import com.hak.wymi.validations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_PASSWORD_STRENGTH = 60;

    @Override
    public void initialize(Password password) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext cxt) {
        Integer passwordStrength = PasswordStrengthChecker.getStrengthResults(password).getFinalResult();

        return password == null || passwordStrength >= MIN_PASSWORD_STRENGTH;
    }

}