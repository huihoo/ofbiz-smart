package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Max;
import org.huihoo.ofbiz.smart.base.validation.Min;

public class MaxValidator implements ConstraintValidator<Max,Number>{

    @Override
    public void initialize(Max constraintAnnotation) {

    }

    @Override
    public boolean isValid(Number value) {
        return false;
    }
}
