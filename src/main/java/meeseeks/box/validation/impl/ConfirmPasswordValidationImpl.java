package meeseeks.box.validation.impl;

import meeseeks.box.validation.ConfirmPasswordValidation;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidationImpl implements ConstraintValidator<ConfirmPasswordValidation, Object> {

    private String passwordField;
    private String confirmPasswordField;
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public void initialize(ConfirmPasswordValidation constraintAnnotation) {
        passwordField = constraintAnnotation.value()[0];
        confirmPasswordField = constraintAnnotation.value()[1];
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        final String password = (String) PARSER.parseExpression(passwordField).getValue(object);
        final String confirmPassword = (String) PARSER.parseExpression(confirmPasswordField).getValue(object);

        return password.equals(confirmPassword);
    }
}