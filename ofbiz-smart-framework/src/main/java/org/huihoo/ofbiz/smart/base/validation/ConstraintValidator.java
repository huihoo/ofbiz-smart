package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Annotation;

/**
 * <p>
 *     验证器接口
 * </p>
 * @since  1.0
 * @param <A>
 * @param <T>
 */
public interface ConstraintValidator<A extends Annotation,T> {
    /**
     * 初始化
     * @param constraintAnnotation 指定的验证注解对象
     */
    void initialize(A constraintAnnotation);

    /**
     * 验证指定的值是否有效
     * @param value   要验证的值
     * @return 验证通过，返回<code>true</code>;否则返回<code>false</code>
     */
    boolean isValid(T value);

}
