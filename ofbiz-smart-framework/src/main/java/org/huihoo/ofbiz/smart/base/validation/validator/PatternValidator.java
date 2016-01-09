package org.huihoo.ofbiz.smart.base.validation.validator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Pattern;



public class PatternValidator implements ConstraintValidator<Pattern, CharSequence> {

  private java.util.regex.Pattern pattern;

  @Override
  public void initialize(Pattern constraintAnnotation) {
    this.pattern = java.util.regex.Pattern.compile(constraintAnnotation.value());
  }

  @Override
  public boolean isValid(CharSequence value) {
    if (CommUtil.isEmpty(value)) {
      return true;
    }
    return pattern.matcher(value).matches();
  }
}
