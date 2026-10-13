package com.panut.centralbanking.customerservice.infrastructure.fields;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$")
public @interface NameFormat {
    String message() default "Invalid name format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
