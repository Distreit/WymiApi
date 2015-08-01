package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Override
    public void initialize(Email email) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        if (email == null) {
            return false;
        }
        return email.matches(".+@.+\\..+");
    }

}