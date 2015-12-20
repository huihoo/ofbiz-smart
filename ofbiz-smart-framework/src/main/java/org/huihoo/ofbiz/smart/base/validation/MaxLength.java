package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 *    检查要验证的字符串长度是否小于或等于指定的最大值
 * </p>
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface MaxLength {

    String message() default "This value is too long. It should have %s characters or fewer.";

    long value();

    ValidateProfile profile() default ValidateProfile.ALL;

}
