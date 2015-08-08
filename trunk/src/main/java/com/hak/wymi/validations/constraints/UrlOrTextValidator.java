package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.validations.UrlOrText;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UrlOrTextValidator implements ConstraintValidator<UrlOrText, Object> {
    @Override
    public void initialize(UrlOrText urlOrText) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object instanceof Post) {
            Post post = (Post) object;
            if (post.getIsText()) {
                return post.getText() != "";
            } else {
                return UrlValidator.isValid(post.getUrl());
            }
        }
        return false;
    }

}