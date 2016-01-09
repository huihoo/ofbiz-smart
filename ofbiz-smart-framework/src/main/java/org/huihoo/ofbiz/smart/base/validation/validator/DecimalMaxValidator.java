package org.huihoo.ofbiz.smart.base.validation.validator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.DecimalMax;

import java.math.BigDecimal;

public class DecimalMaxValidator implements ConstraintValidator<DecimalMax, CharSequence> {

  private BigDecimal maxValue;

  @Override
  public void initialize(DecimalMax constraintAnnotation) {
    this.maxValue = new BigDecimal(constraintAnnotation.value());
  }

  @Override
  public boolean isValid(CharSequence value) {
    if (CommUtil.isEmpty(value)) {
      return true;
    }
    try {
      return new BigDecimal(value.toString()).compareTo(maxValue) <= 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
