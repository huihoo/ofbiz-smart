package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * <p>
 * 检查要验证的对象不能为空，即不能等于<code>Null</code>和空值
 * </p>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface Required {

  String message() default "This value is required.";

  ValidateProfile profile() default ValidateProfile.ALL;
}
