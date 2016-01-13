package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 检查要验证的字符串是否是有效的邮箱
 * </p>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface Email {
  String message() default "This value should be a valid email.";

  ValidateProfile profile() default ValidateProfile.ALL;
}
