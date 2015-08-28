package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.Post;
import com.hak.wymi.validations.UrlOrText;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UrlOrTextValidator implements ConstraintValidator<UrlOrText, Post> {
    @Override
    public void initialize(UrlOrText urlOrText) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Post post, ConstraintValidatorContext cxt) {
        if (post.getIsText()) {
            return post.getText() != null && !"".equals(post.getText());
        }
        return UrlValidatorConstraint.isValidUrl(post.getUrl());
    }

}