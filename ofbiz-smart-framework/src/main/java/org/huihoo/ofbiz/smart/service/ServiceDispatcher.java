package org.huihoo.ofbiz.smart.service;


import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;
import org.huihoo.ofbiz.smart.service.engine.GenericEngine;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDispatcher {
  private final static String TAG = ServiceDispatcher.class.getName();
  
  private final static String SLOW_LOG_TAG = "ServiceCallSlowLog";
  
  /** 执行引擎缓存 */
  private final static Map<String, GenericEngine> ENGINE_MAP = new ConcurrentHashMap<>();

  /** 服务回调接口缓存 */
  private final static Map<String, ServiceCallback> SERVICE_CALLBACK_MAP = new ConcurrentHashMap<>();

  /** 所有服务定义的上下文信息缓存 */
  private final static Map<String, ServiceModel> SERVICE_CONTEXT_MAP = new ConcurrentHashMap<>();
  
  /** 内置的服务执行引擎 */
  private final static String[] INTENAL_ENGINES = {
                                    "org.huihoo.ofbiz.smart.service.engine.EntityAutoEngine",
                                    "org.huihoo.ofbiz.smart.service.engine.StandardJavaEngine"
  };

  /** 当前服务执行的环境 是开发环境还是生产环境 */
  private volatile String profile;
  
  /** 服务执行过慢的毫秒 */
  private volatile int slowTimeInMilliSeconds;
  
  /** 当前属性配置 */
  private volatile Properties applicationConfig;

  /** 服务执行依赖的数据库访问代理对象 */
  private final Delegator delegator;

  /** 要扫描的服务资源名称，可以是服务类所在包名，也可以是服务类打包的Jar包路径，多个以逗号隔开*/
  private final String scanResNames;

  public ServiceDispatcher(Delegator delegator) throws GenericServiceException {
    applicationConfig = new Properties();
    try {
      applicationConfig.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
    } catch (IOException e) {
      throw new GenericServiceException("Unable to load external properties");
    }

    scanResNames = applicationConfig.getProperty(C.SERVICE_SCANNING_NAMES);
    profile = applicationConfig.getProperty(C.PROFILE_NAME);
    slowTimeInMilliSeconds = Integer.valueOf(applicationConfig.getProperty(C.SERVICE_SLOWTIME_MILLISECONDS, "30000"));
    if (CommUtil.isEmpty(scanResNames)) {
      throw new GenericServiceException("Config[service.scanning.names] is empty.");
    }

    if (delegator == null) {
      Log.w("[ServiceDispatcher.init]:Could not find Delegator instance", TAG);
    }
    
    this.delegator = delegator;

    for (String engineClazzName : INTENAL_ENGINES) {
      registerEngine(engineClazzName);
    }

    loadAndFilterServiceClazz();
  }


  public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) {
    long beginTime = System.currentTimeMillis();
    boolean transaction = false;
    boolean persist = false;
    boolean exception = false;
   
    try {
      if (!C.PROFILE_PRODUCTION.equals(profile)) {
        // In test,develop profile, always load it.
        loadAndFilterServiceClazz();
      }
      ServiceModel serviceModel = SERVICE_CONTEXT_MAP.get(serviceName);
      if (serviceModel == null) {
        String msg = "Unable to locate service[" + serviceName + "]";
        Log.w(msg, TAG);
        return ServiceUtil.returnProplem("SERVICE_NOT_FOUND", msg);
      }
      
      GenericEngine engine = ENGINE_MAP.get(serviceModel.engineName);
      if (engine == null) {
        Log.w("Unsupported ServiceEngine [%s]", TAG, serviceModel.engineName);
        return ServiceUtil.returnProplem("UNSUPPORTED_SERVICE_ENGINE", "Unsupported service ["+ serviceName + "]");
      }
      
      transaction = serviceModel.transaction;
      persist = serviceModel.persist;
      
      if (persist && delegator == null) {
        Log.w("Service [%s] require persist context.", TAG, serviceName);
        return ServiceUtil.returnProplem("UNSUPPORTED_SERVICE_ENGINE", "Unsupported service ["+ serviceName + "]");
      }
      
      ctx.put(C.APPLICATION_CONFIG_PROP_KEY, applicationConfig);
      
      if (persist && transaction) {
        delegator.beginTransaction();
      }
      //TODO 输入参数的验证
      
      //TODO 服务的认证
      
      Map<String,Object> result = engine.runSync(serviceName, ctx);
      
      //TODO 输出参数的验证
      
      
      //TODO 服务回调的执行
      
      return result;
    } catch (Exception e) {
      if (persist && transaction && delegator != null) {
        delegator.rollback();
      }
      Log.e(e, e.getMessage(), TAG);
      exception = true;
      return ServiceUtil.returnProplem("SERVICE_CALL_EXCEPTION", "Calling service["+serviceName+"] has an exception.");
    } finally {
      if (persist && transaction && delegator != null) {
        delegator.endTransaction();
      }
      long costTime = (System.currentTimeMillis() - beginTime);
      if (exception) {
        Log.i("Service[%s] cost %s ms. But exception happend.", TAG, serviceName,costTime);
      } else {
        Log.i("Service[%s] cost %s ms.", TAG, serviceName,costTime);
      }
      
      if (costTime > slowTimeInMilliSeconds) {
        Log.w("Service[%s] cost %s ms. It has been exceed a threshold value[%s]", SLOW_LOG_TAG, serviceName,costTime,slowTimeInMilliSeconds);
      }
    }
  }

  public void registerEngine(String engineClazzName) {
    if (CommUtil.isEmpty(engineClazzName)) {
      return;
    }
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> c = loader.loadClass(engineClazzName);
      Constructor<GenericEngine> cn = CommUtil.cast(c.getConstructor(ServiceDispatcher.class));
      GenericEngine engine = cn.newInstance(this);
      ENGINE_MAP.put(engine.getName(), engine);
    } catch (NoSuchMethodException e) {
      Log.e(e, "Unable to register engine[" + engineClazzName + "]", TAG);
    } catch (ClassNotFoundException e) {
      Log.e(e, "Unable to register engine[" + engineClazzName + "]", TAG);
    } catch (IllegalAccessException e) {
      Log.e(e, "Unable to register engine[" + engineClazzName + "]", TAG);
    } catch (InstantiationException e) {
      Log.e(e, "Unable to register engine[" + engineClazzName + "]", TAG);
    } catch (InvocationTargetException e) {
      Log.e(e, "Unable to register engine[" + engineClazzName + "]", TAG);
    }
  }


  public void registerCallback(String serviceCallbackClazzName) {
    if (CommUtil.isEmpty(serviceCallbackClazzName)) {
      return;
    }
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> c = loader.loadClass(serviceCallbackClazzName);
      Constructor<ServiceCallback> cn = CommUtil.cast(c.getConstructor());
      ServiceCallback serviceCallback = cn.newInstance();
      SERVICE_CALLBACK_MAP.put(serviceCallback.getClass().getName(), serviceCallback);
    } catch (NoSuchMethodException e) {
      Log.e(e, "Unable to register serviceCallback[" + serviceCallbackClazzName + "]", TAG);
    } catch (ClassNotFoundException e) {
      Log.e(e, "Unable to register serviceCallback[" + serviceCallbackClazzName + "]", TAG);
    } catch (IllegalAccessException e) {
      Log.e(e, "Unable to register serviceCallback[" + serviceCallbackClazzName + "]", TAG);
    } catch (InstantiationException e) {
      Log.e(e, "Unable to register serviceCallback[" + serviceCallbackClazzName + "]", TAG);
    } catch (InvocationTargetException e) {
      Log.e(e, "Unable to register serviceCallback[" + serviceCallbackClazzName + "]", TAG);
    }
  }
  
  
  public void registerService(ServiceModel sm) {
    if (CommUtil.isEmpty(sm) || CommUtil.isEmpty(sm.name)) {
      return;
    }
    SERVICE_CONTEXT_MAP.put(sm.name, sm);
  }

  public Map<String, ServiceModel> getServiceContextMap() {
    return SERVICE_CONTEXT_MAP;
  }

  // ===================================================================
  // Private Method
  // ===================================================================
  private void loadAndFilterServiceClazz() {
    Set<Class<?>> serviceClasses = new LinkedHashSet<Class<?>>();
    String[] scanResNamesArray = scanResNames.split(",");
    
    //NOTICE 如果scanResName为org.huihoo.ofbiz.smart.service
    //NOTICE 会引发StackOverFlow异常，因为执行该方法的类就在这个包下面，
    //NOTICE 引起了递归执行，无法退出
    for (String scanResName : scanResNamesArray) {
      if (getClass().getPackage().equals(scanResName)) {
        Log.w("Resource name [" + getClass().getPackage() + "] ignored.", TAG);
        continue;
      }
      serviceClasses.addAll(scanServiceClazz(scanResName, true));
    }

    for (Class<?> sClazz : serviceClasses) {
      Service serviceAnno = sClazz.getAnnotation(Service.class);
      if (serviceAnno == null) {
        continue;
      }
      
      Method[] methods = sClazz.getMethods();
      for (Method method : methods) {
        ServiceDefinition sd = method.getAnnotation(ServiceDefinition.class);
        if (sd == null) {
          continue;
        }
        Log.d("Service [%s][%s] found.", TAG,sClazz.getName(),method.getName());
        ServiceModel sm = new ServiceModel();
        sm.engineName = sd.type();
        sm.entityName = sd.entityName();
        sm.location = sClazz.getName();
        sm.invoke = method.getName();
        sm.name = sd.name();
        sm.description = sd.description();
        sm.transaction = sd.transaction();
        sm.export = sd.export();
        sm.persist = sd.persist();
        sm.callback = sd.callback();
        sm.requireAuth = sd.requireAuth();
        if (sm.callback != null && sm.callback.length > 0) {
          for (Class<?> clz : sm.callback) {
            try {
              Constructor<ServiceCallback> cn = CommUtil.cast(clz.getConstructor());
              ServiceCallback serviceCallback = cn.newInstance();
              SERVICE_CALLBACK_MAP.put(clz.getName(), serviceCallback);
            } catch(Exception e) {
              Log.w("Unable to load service callback class [%s]", TAG, clz);
            }
          }
        }
        SERVICE_CONTEXT_MAP.put(sm.name, sm);
      }
    }
  }
  
  private Set<Class<?>> scanServiceClazz(String resourceName, boolean recursive) {
    Set<Class<?>> serviceClazzSet = new LinkedHashSet<>();
    if (resourceName.endsWith(".jar")) {
      // TODO jar
    } else {
      String pkgDirName = resourceName.replaceAll("\\.", "/");
      try {
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(pkgDirName);
        while (resources.hasMoreElements()) {
          URL url = resources.nextElement();
          String protocal = url.getProtocol();
          if ("file".equals(protocal)) {
            String filePath = URLDecoder.decode(url.getFile(), C.UTF_8);
            findAndAddServiceClazz(resourceName, filePath, recursive, serviceClazzSet);
          }
        }
      } catch (IOException e) {
        Log.w("Could not find resource [" + pkgDirName + "]", TAG);
      }
    }
    return serviceClazzSet;
  }


  private void findAndAddServiceClazz(String pkg, String pgkPath, final boolean recursive, Set<Class<?>> classes) {
    File dir = new File(pgkPath);
    if (!dir.exists() && !dir.isDirectory()) {
      return;
    }

    File[] files = dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
      }
    });
    
    for (File f : files) {
      if (f.isDirectory()) {
        findAndAddServiceClazz(pkg, pgkPath, recursive, classes);
      } else {
        // remove .class suffix
        String clazzName = f.getName().substring(0, f.getName().length() - 6);
        try {
          classes.add(Thread.currentThread().getContextClassLoader().loadClass(pkg + "." + clazzName));
        } catch (ClassNotFoundException e) {
          Log.w("Class[" + pkg + clazzName + "] not found.", TAG);
        }
      }
    }
  }
}
