package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Override
    public void initialize(Email email) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        return email == null || email.toUpperCase().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
    }

}