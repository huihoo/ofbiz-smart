package org.huihoo.ofbiz.smart.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.huihoo.ofbiz.smart.service.ServiceCallback;



/**
 *
 * <P>
 * 具体的服务定义
 * </P>
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ServiceDefinition {

  /** 服务的名称 */
  String name();

  /** 服务的类型 */
  String type() default "java";

  /** 服务的描述 */
  String description() default "";

  /** 是否启用事务 */
  boolean transaction() default false;

  /** 是否持久化 */
  boolean persist() default true;

  /** 是否需要身份认证 */
  boolean requireAuth() default false;

  /** 是否对外提供远程调用 */
  boolean export() default false;

  /** 实体的名称(如果有) */
  String entityName() default "";

  /** 服务回调数组 */
  Class<ServiceCallback>[] callback() default {};

  /** 服务参数数组 */
  Parameter[] parameters() default @Parameter(name = "");

  /** JSON格式的返回示例 */
  String responseJsonExample() default "";

  /** XML格式的返回示例 */
  String responseXmlExample() default "";
}
