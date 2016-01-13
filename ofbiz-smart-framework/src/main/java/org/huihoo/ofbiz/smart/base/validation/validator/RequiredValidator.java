package org.huihoo.ofbiz.smart.base.validation.validator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Required;

public class RequiredValidator implements ConstraintValidator<Required, Object> {

  @Override
  public void initialize(Required constraintAnnotation) {}

  @Override
  public boolean isValid(Object value) {
    return CommUtil.isNotEmpty(value);
  }
}
