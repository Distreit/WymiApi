package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.EmailsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailsMatchValidator implements ConstraintValidator<EmailsMatch, User> {
    @Override
    public void initialize(EmailsMatch emailsMatch) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext cxt) {
        return user.getEmail().equalsIgnoreCase(user.getConfirmEmail());
    }

}