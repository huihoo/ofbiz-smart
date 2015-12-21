package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Min;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MinValidator implements ConstraintValidator<Min, Number> {

  private long minValue;

  @Override
  public void initialize(Min constraintAnnotation) {
    this.minValue = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(Number value) {
    // 为空可以认为是有效的
    if (value == null) {
      return true;
    }

    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).compareTo(BigDecimal.valueOf(minValue)) != -1;
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).compareTo(BigInteger.valueOf(minValue)) != -1;
    } else {
      long longValue = value.longValue();
      return longValue >= minValue;
    }
  }
}
