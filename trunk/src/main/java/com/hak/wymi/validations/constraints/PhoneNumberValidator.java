package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public void initialize(PhoneNumber email) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext cxt) {
        return phoneNumber == null || phoneNumber.toUpperCase().matches("^\\d{10}$");
    }

}