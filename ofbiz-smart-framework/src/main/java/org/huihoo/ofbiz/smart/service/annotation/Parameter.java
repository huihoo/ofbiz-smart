package org.huihoo.ofbiz.smart.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <P>
 * 服务参数
 * </P>
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Parameter {
  /** 参数名称 */
  String name();

  /** 参数类型 */
  Class<?> type() default String.class;

  /** 参数输入输出模式,仅支持 IN(输入),OUT(输出),IN_AND_OUT(即是输入，又可以是输出) */
  String mode() default "IN";

  /** 参数是否可选 */
  boolean optinal() default true;

  /** 参数描述 */
  String description() default "";

  /** 参数的值是否必须 */
  boolean valueReqiured() default false;

  /** 参数的默认值 */
  String defaultValue() default "";
  
  /** 参数的获取范围 context(直接在服务上下文获取) session(直接从会话中获取) config(直接从属性配置文件中获取) */
  String scope() default "context";
}
