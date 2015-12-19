package org.huihoo.ofbiz.smart.base.validation;


public @interface Null {
    String message() default "This value should be NULL.";
}
