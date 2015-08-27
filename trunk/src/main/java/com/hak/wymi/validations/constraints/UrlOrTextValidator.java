package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.validations.UrlOrText;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UrlOrTextValidator implements ConstraintValidator<UrlOrText, Object> {
    @Override
    public void initialize(UrlOrText urlOrText) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object instanceof Post) {
            final Post post = (Post) object;
            if (post.getIsText()) {
                return !post.getText().equals("");
            } else {
                return UrlValidator.isValidUrl(post.getUrl());
            }
        }
        return false;
    }

}