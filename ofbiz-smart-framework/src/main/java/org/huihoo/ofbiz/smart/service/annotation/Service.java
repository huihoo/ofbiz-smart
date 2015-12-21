package org.huihoo.ofbiz.smart.service.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识指定的类是一个服务类
 *
 * @since 1.0
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Service {}
