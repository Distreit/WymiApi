package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
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

        return total >= 3 && password.length() >= 8;
    }

}