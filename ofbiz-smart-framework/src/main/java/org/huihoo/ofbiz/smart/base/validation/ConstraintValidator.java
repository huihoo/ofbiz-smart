package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Annotation;

public interface ConstraintValidator<A extends Annotation,T> {

    void initialize(A constraintAnnotation);

    boolean isValid(T value);

}
