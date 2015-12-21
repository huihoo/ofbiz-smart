package org.huihoo.ofbiz.smart.entity;

/**
 * <P>
 * 事务执行接口，执行成功后，返回业务需要的<code>Object</code>
 * </P>
 */
public interface TxCallable {

  Object call();
}
