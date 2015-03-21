package org.huihoo.ofbiz.smart.service.engine;

import java.util.Map;

import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;






/**
 * <p>
 * 抽象的异步服务引擎类，继承自{@link com.dnx.service.engine.AbstractEngine}<br/>
 * 它实现了服务的异步执行。所有的服务引擎实现类，都应该继承该类。
 * </p>
 * 
 * @author huangbohua
 *
 */
public abstract class GenericAsyncEngine extends AbstractEngine {


  public GenericAsyncEngine(ServiceDispatcher dispatcher) {
    super(dispatcher);
  }

  public abstract Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException;

  @Override
  public void runAsync(String localName, Map<String, Object> context)
          throws GenericServiceException {
    // TODO 异步执行

  }

}
