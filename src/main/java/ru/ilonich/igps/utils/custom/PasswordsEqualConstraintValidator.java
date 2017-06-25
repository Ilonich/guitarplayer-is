package ru.ilonich.igps.utils.custom;

import ru.ilonich.igps.to.RegisterTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class PasswordsEqualConstraintValidator implements
        ConstraintValidator<PasswordsEqualConstraint, Object> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        RegisterTO user = (RegisterTO) candidate;
        return user.getPassword().equals(user.getPasswordcopy());
    }
}