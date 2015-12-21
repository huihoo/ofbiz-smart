package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 检查要验证的字符串长度是否大于或等于指定的最小值
 * </p>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface MinLength {

  String message() default "This value is too short. It should have %s characters or more.";

  long value();

  ValidateProfile profile() default ValidateProfile.ALL;

}
