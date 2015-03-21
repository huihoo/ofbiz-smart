package org.huihoo.ofbiz.smart.service;

import java.util.Map;

/**
 * <p>
 * 服务事件处理接口类
 * </p>
 * 
 * @author huangbohua
 *
 */
public interface ServiceEventActionInterface {
  /**
   * <p>
   * 在服务调用之前执行
   * </p>
   * 
   * @param context 要调用的服务上下文<code>Map</code>对象
   */
  public void before(Map<String, Object> context);

  /**
   * <p>
   * 在服务调用成功之后执行
   * </p>
   * 
   * @param context 要调用的服务上下文<code>Map</code>对象
   * @param result 服务调用完成后<code>Map</code>对象
   */
  public void success(Map<String, Object> context, Map<String, Object> result);
}
