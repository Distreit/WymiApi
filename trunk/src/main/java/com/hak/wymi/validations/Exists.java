package com.hak.wymi.validations;

import com.hak.wymi.validations.constraints.ExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExistsValidator.class)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {

    String message() default "Exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean shouldExist();

    String stringType() default "";
}
