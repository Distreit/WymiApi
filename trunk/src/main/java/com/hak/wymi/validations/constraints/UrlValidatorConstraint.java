package com.hak.wymi.validations.constraints;

import com.hak.wymi.validations.Url;
import org.apache.commons.validator.routines.UrlValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UrlValidatorConstraint implements ConstraintValidator<Url, String> {
    public static final UrlValidator validator =
            new UrlValidator(
                    new String[]{"http", "https"},
                    UrlValidator.NO_FRAGMENTS
            );

    public static boolean isValidUrl(String url) {
        if (url == null) {
            return false;
        }

        return UrlValidatorConstraint.validator.isValid(url);
    }

    @Override
    public void initialize(Url url) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext cxt) {
        return UrlValidatorConstraint.isValidUrl(url);
    }
}