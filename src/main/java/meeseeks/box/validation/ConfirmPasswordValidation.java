package meeseeks.box.validation;

import meeseeks.box.validation.impl.ConfirmPasswordValidationImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmPasswordValidationImpl.class)
public @interface ConfirmPasswordValidation {
    String[] value();

    String message() default "{confirmPassword.mismatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}