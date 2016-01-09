package org.huihoo.ofbiz.smart.base.validation.validator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.DecimalMin;

import java.math.BigDecimal;

public class DecimalMinValidator implements ConstraintValidator<DecimalMin, CharSequence> {

  private BigDecimal minValue;

  @Override
  public void initialize(DecimalMin constraintAnnotation) {
    this.minValue = new BigDecimal(constraintAnnotation.value());
  }

  @Override
  public boolean isValid(CharSequence value) {
    if (CommUtil.isEmpty(value)) {
      return true;
    }
    try {
      return new BigDecimal(value.toString()).compareTo(minValue) >= 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
