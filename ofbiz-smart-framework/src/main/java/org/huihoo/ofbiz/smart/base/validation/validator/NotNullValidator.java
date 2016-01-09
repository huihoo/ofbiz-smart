package org.huihoo.ofbiz.smart.base.validation.validator;


import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Null;

public class NotNullValidator implements ConstraintValidator<Null, Object> {



  @Override
  public void initialize(Null constraintAnnotation) {}

  @Override
  public boolean isValid(Object value) {
    return value != null;
  }
}
