package org.huihoo.ofbiz.smart.service.engine;

import java.util.Map;

import org.huihoo.ofbiz.smart.service.GenericServiceException;





/**
 * <p>
 * 服务引擎接口类，它定义了服务引擎必须实现的接口
 * </p>
 * 
 * @author huangbohua
 *
 */
public interface GenericEngine {
  /**
   * <p>
   * 同步执行服务并返回执行结果
   * </p>
   * 
   * @param localName 要执行的服务名称
   * @param context 服务执行上下文<code>Map</code>对象实例
   * @return 服务返回<code>Map</code>对象实例
   * @throws GenericServiceException
   */
  public Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException;


  /**
   * <p>
   * 异步执行服务
   * </p>
   * 
   * @param localName 要执行的服务名称
   * @param context 服务执行上下文<code>Map</code>对象实例
   * @throws GenericServiceException
   */
  public void runAsync(String localName, Map<String, Object> context)
          throws GenericServiceException;

}
