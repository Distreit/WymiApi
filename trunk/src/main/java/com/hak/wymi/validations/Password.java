package com.hak.wymi.validations;

import com.hak.wymi.validations.constraints.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Passwords must be at least 8 characters and contain three of the following: numbers, lower-case letters, upper-case letters, and symbols";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}