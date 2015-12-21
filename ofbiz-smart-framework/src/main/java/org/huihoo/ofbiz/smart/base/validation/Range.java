package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 检查要验证的<b>整数</b>是否在指定最小值和最大值之间，即：大于或等于 <b>指定的最小值</b> 并且 是否小于或等于 <b>指定的最大值</b>
 * </p>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface Range {

  String message() default "This value should be between %s and %s.";

  long min() default 0;

  long max() default Long.MAX_VALUE;

  ValidateProfile profile() default ValidateProfile.ALL;
}
