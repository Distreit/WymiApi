package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, User> {
    @Override
    public void initialize(PasswordsMatch passwordsMatch) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext cxt) {
        return user.getPassword().equals(user.getConfirmPassword());
    }

}