package org.huihoo.ofbiz.smart.service;

import java.net.URL;

public abstract class ServiceResource {
  /**
   * <p>
   * 获取服务配置文件所在的跟目录
   * </p>
   * 
   * @return
   */
  public abstract URL getBaseURL();
}
