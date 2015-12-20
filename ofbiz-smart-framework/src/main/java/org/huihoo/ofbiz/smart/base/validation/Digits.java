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
 *    检查要验证的字符串是否是有效的数字
 * </p>
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface Digits {
    String message() default "This value should be valid digits";

    /** 小数点前的整数部分最大位数 */
    int integer();

    /** 小数点后小数最大位数*/
    int fraction() default  2;

    ValidateProfile profile() default ValidateProfile.ALL;
}
