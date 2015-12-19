package org.huihoo.ofbiz.smart.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * <P>
 *     具体的服务定义
 * </P>
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
public @interface ServiceDefinition {

    /** 服务的名称 */
    String name();

    /** 服务的类型 */
    String type();

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

    String entityName() default "";

    /** 输入参数集合 */
    InParameter[]  inParameters() default @InParameter(name = "");

    /** 输出参数集合 */
    OutParameter[] outParameters() default @OutParameter(name = "");
}
