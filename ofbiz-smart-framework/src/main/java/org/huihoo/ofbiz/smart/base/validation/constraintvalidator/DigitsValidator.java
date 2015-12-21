package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Digits;

import java.math.BigDecimal;

public class DigitsValidator implements ConstraintValidator<Digits, CharSequence> {

  private int maxIntegerLength;
  private int maxFractionLength;

  @Override
  public void initialize(Digits constraintAnnotation) {
    this.maxIntegerLength = constraintAnnotation.integer();
    this.maxFractionLength = constraintAnnotation.fraction();
  }

  @Override
  public boolean isValid(CharSequence value) {
    if (CommUtil.isEmpty(value)) {
      return true;
    }

    BigDecimal bigNum = new BigDecimal(value.toString()).stripTrailingZeros();
    int integerPartLength = bigNum.precision() - bigNum.scale();
    int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

    return (maxIntegerLength >= integerPartLength && maxFractionLength >= fractionPartLength);
  }
}
