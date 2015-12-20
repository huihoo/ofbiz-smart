package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Min;

public class MinValidator implements ConstraintValidator<Min,Number>{

    @Override
    public void initialize(Min constraintAnnotation) {

    }

    @Override
    public boolean isValid(Number value) {
        return false;
    }
}
