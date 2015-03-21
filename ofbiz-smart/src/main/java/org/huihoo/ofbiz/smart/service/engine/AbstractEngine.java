package org.huihoo.ofbiz.smart.service.engine;

import org.huihoo.ofbiz.smart.service.ServiceDispatcher;








/**
 * <p>
 *  抽象服务引擎类，它继承自{@link com.dnx.service.engine.GenericEngine}<br/>
 *  同时，它定义了每个服务引擎实现类 必须继承的构造方法
 * </p>
 * @author huangbohua
 *
 */
public abstract class AbstractEngine implements GenericEngine {
  protected ServiceDispatcher dispatcher = null;

  public AbstractEngine(ServiceDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }
}
