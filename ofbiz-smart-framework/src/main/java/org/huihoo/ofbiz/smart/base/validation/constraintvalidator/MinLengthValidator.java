package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.MinLength;

public class MinLengthValidator implements ConstraintValidator<MinLength, CharSequence> {

  private long minLength;

  @Override
  public void initialize(MinLength constraintAnnotation) {
    this.minLength = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(CharSequence value) {
    // 为空可以认为是有效的
    if (value == null) {
      return true;
    }
    int length = value.length();
    return length >= minLength;
  }
}
