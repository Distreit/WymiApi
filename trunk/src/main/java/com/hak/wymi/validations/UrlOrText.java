package com.hak.wymi.validations;

import com.hak.wymi.validations.constraints.UrlOrTextValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UrlOrTextValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlOrText {
    String message() default "Url or text not set correctly";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}