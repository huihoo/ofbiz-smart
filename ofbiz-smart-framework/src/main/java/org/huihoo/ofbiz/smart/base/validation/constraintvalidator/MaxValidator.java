package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Max;
import org.huihoo.ofbiz.smart.base.validation.Min;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MaxValidator implements ConstraintValidator<Max,Number>{

    private long maxValue;

    @Override
    public void initialize(Max constraintAnnotation) {
        this.maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value) {
        // 为空可以认为是有效的
        if (value == null) {
            return true;
        }

        if (value instanceof BigDecimal) {
            return ( (BigDecimal) value ).compareTo( BigDecimal.valueOf( maxValue ) ) != -1;
        } else if (value instanceof BigInteger) {
            return ( (BigInteger) value ).compareTo( BigInteger.valueOf( maxValue ) ) != -1;
        } else {
            long longValue = value.longValue();
            return longValue <= maxValue;
        }
    }
}
