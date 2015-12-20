package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface DecimalMin {
    String message() default "This value should be less than or equal to %s.";

    String value();

    ValidateProfile profile() default ValidateProfile.ALL;
}
