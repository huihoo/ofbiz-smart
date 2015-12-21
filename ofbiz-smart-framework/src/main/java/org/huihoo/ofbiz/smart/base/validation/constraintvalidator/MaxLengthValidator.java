package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.MaxLength;

public class MaxLengthValidator implements ConstraintValidator<MaxLength,CharSequence>{

    private long maxLength;

    @Override
    public void initialize(MaxLength constraintAnnotation) {
        this.maxLength = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(CharSequence value) {
        // 为空可以认为是有效的
        if (value == null) {
            return true;
        }
        int length = value.length();
        return length <= maxLength;
    }
}
