package org.huihoo.ofbiz.smart.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <P>
 * 指定服务的输出参数
 * </P>
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface OutParameter {
  /** 参数名称 */
  String name();

  /** 参数描述 */
  String description() default "";

  /** 参数是否必须 */
  boolean required() default false;

  /** 参数的默认值 */
  String defaultValue() default "";

  /** 参数类型 */
  Class<?> type() default String.class;
}
