package org.huihoo.ofbiz.smart.base.validation;


public @interface NotNull {
    String message() default "This value should be not NULL.";
}
