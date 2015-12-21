package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.DecimalRange;

import java.math.BigDecimal;

public class DecimalRangeValidator implements ConstraintValidator<DecimalRange,CharSequence>{

    private BigDecimal minValue;
    private BigDecimal maxValue;

    @Override
    public void initialize(DecimalRange constraintAnnotation) {
        this.maxValue = new BigDecimal(constraintAnnotation.max());
        this.minValue = new BigDecimal(constraintAnnotation.min());
    }

    @Override
    public boolean isValid(CharSequence value) {
        if (CommUtil.isEmpty(value)) {
            return true;
        }
        try {
            BigDecimal bigNum = new BigDecimal(value.toString());
            return bigNum.compareTo(minValue) >= 0 && bigNum.compareTo(maxValue) <= 0;
        }catch (NumberFormatException e) {
            return false;
        }
    }
}
