package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface Range {

    String message() default "This value should be between %s and %s.";

    long min() default 0;
    long max() default Long.MAX_VALUE;
}
