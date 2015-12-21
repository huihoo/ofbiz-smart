package org.huihoo.ofbiz.smart.base.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Constraint {
  Class<? extends ConstraintValidator<?, ?>>[]validatedBy();
}
