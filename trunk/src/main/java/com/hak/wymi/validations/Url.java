package com.hak.wymi.validations;

import com.hak.wymi.validations.constraints.UrlValidatorConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UrlValidatorConstraint.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Url {
    String message() default "Invalid url";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}