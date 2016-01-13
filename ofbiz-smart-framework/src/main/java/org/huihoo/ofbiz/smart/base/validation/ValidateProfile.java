package org.huihoo.ofbiz.smart.base.validation;

/**
 * <p>
 * 表示验证环境,包括创建实体时验证，更新实体时验证，任意情况都验证。
 * </p>
 * 
 * @since 1.0
 */
public enum ValidateProfile {
  CREATE, UPDATE, ALL;
}
