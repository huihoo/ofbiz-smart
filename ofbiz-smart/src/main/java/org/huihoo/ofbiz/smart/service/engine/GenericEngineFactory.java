package org.huihoo.ofbiz.smart.service.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;




/**
 * <p>
 * 服务引擎工厂类，主要负责服务引擎的获取
 * </p>
 * 
 * @author huangbohua
 *
 */
public class GenericEngineFactory {

  protected ServiceDispatcher dispatcher = null;
  protected Map<String, GenericEngine> engines = null;

  public GenericEngineFactory(ServiceDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    engines = new HashMap<String, GenericEngine>();
  }

  /**
   * <p>
   * 获取服务引擎
   * </p>
   * 
   * @param engineName 要获取的服务引擎名称
   * @return
   * @throws GenericServiceException
   */
  public GenericEngine getGenericEngine(String engineName) throws GenericServiceException {
    String className = null;
    //FIXME 服务引擎的可配置化
    if ("java".equals(engineName)) {
      className = "com.malllike.service.engine.StandardJavaEngine";
    }else if("simple".equals(engineName)){ 
      className = "com.malllike.service.engine.SimpleServiceEngine";
    }else {
      throw new GenericServiceException("不支持的服务引擎[" + engineName + "]");
    }

    GenericEngine engine = engines.get(engineName);
    if (engine == null) {
      synchronized (GenericEngineFactory.class) {
        engine = engines.get(engineName);
        if (engine == null) {
          try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> c = loader.loadClass(className);
            Constructor<GenericEngine> cn =
                    CommUtils.cast(c.getConstructor(ServiceDispatcher.class));
            engine = cn.newInstance(dispatcher);
          } catch (Exception e) {
            throw new GenericServiceException(e.getMessage(), e);
          }
          if (engine != null) {
            engines.put(engineName, engine);
          }
        }
      }
    }

    return engine;
  }


  public Map<String, GenericEngine> getEngines() {
    return engines;
  }

}
