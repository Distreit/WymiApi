package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 8;

    @Override
    public void initialize(Password password) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext cxt) {
        if (password == null) {
            return false;
        }

        int total = 0;
        if (password.matches(".*[a-z].*")) {
            total++;
        }

        if (password.matches(".*[A-Z].*")) {
            total++;
        }

        if (password.matches(".*[0-9].*")) {
            total++;
        }

        if (password.matches(".*[~!@#$%^&*()_+|}{\":?><`\\-=\\]\\[;'\\\\,./ ].*")) {
            total++;
        }

        return total >= MIN_LENGTH && password.length() >= MAX_LENGTH;
    }

}