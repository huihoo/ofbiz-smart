package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 检查要验证的字符串是否匹配指定的正则表达式
 * </p>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface Pattern {

  String message() default "This value seems to be invalid.";

  String value();

  ValidateProfile profile() default ValidateProfile.ALL;
}
