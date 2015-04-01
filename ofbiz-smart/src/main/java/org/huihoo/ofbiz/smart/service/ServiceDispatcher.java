/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.service;



import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ModelService.ServiceEventAction;
import org.huihoo.ofbiz.smart.service.engine.GenericEngine;
import org.huihoo.ofbiz.smart.service.engine.GenericEngineFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * <p>
 * 全局服务转发类,负责所有的服务调用的转发
 * </p>
 * 
 * @author huangbohua
 * 
 */
public class ServiceDispatcher {
  private static final String module = ServiceDispatcher.class.getName();
  protected boolean isTestMode = false;
  protected Delegator delegator = null;
  protected GenericEngineFactory factory = null;
  protected Map<String, ModelService> localContext = null;
  protected String serviceResourceName = null;


  public ServiceDispatcher(Delegator delegator) throws GenericServiceException {
    this(delegator, null);
  }


  public ServiceDispatcher(Delegator delegator, String serviceResourceName)
          throws GenericServiceException {
    factory = new GenericEngineFactory(this);
    this.delegator = delegator;
    this.serviceResourceName = serviceResourceName;
    this.localContext = this.getGlobalServiceMap();
    if (delegator == null) {
      Debug.logWaring("[ServiceDispatcher初始化] : 未找到Delegator实例，不能应用于持久化场景.", module);
    }
  }

  public ModelService getLocalContext(String localName) throws GenericServiceException {
    if (this.localContext == null) throw new GenericServiceException();
    ModelService modelService = localContext.get(localName);
    if (modelService == null) {
      Debug.logError("ModelService[" + localName + "]未找到.", module);
    }
    return modelService;
  }


  private Map<String, ModelService> getGlobalServiceMap() throws GenericServiceException {
    Map<String, ModelService> gobalLocalcontext = new TreeMap<String, ModelService>();
    SAXParserFactory parserFactor = SAXParserFactory.newInstance();
    try {
      // 1. 尝试用服务资源实现类来去获取服务配置目录
      URL resourceURL = null;
      if (CommUtils.isNotEmpty(this.serviceResourceName)) {
        Class<?> resourceClazz =
                ServiceDispatcher.class.getClassLoader().loadClass(this.serviceResourceName);
        ServiceResource sr = (ServiceResource) resourceClazz.newInstance();
        resourceURL = sr.getBaseURL();
      }
      // 2.如果无法获取，再在当前线程的执行ClassPath中读取配置目录
      if (resourceURL == null) resourceURL = ServiceDispatcher.class.getResource("/");

      if (resourceURL == null) throw new GenericServiceException("无法获取服务配置目录");

      if (resourceURL.getProtocol().equals("file")) {
        String filePath = resourceURL.getFile();
        Debug.logInfo("服务配置当前类加载所在目录 : " + filePath, module);
        File file = new File(filePath.substring(0, filePath.lastIndexOf("classes")) + "classes");
        Debug.logInfo("服务配置当前类加载所在目录classPath根目录:" + file, module);
        if (file.isDirectory()) {
          File[] files = file.listFiles();
          for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String fName = f.getName();
            if (fName.endsWith(".xml") && fName.startsWith("service")) {
              SAXParser parser = parserFactor.newSAXParser();
              ServiceSaxHandler saxHandler = new ServiceSaxHandler(gobalLocalcontext);
              Debug.logInfo("ServiceFile>" + f, module);
              parser.parse(f, saxHandler);
            }
          }
        }
      } else if (resourceURL.getProtocol().equals("jar")) {
        Debug.logInfo("服务资源文件位于Jar文件之中", module);
        String jarPath =
                URLDecoder.decode(
                        resourceURL.getPath().substring(5, resourceURL.getPath().indexOf("!")),
                        "UTF_8");
        readFromZipFile(jarPath, parserFactor, gobalLocalcontext);
      } else {
        throw new GenericServiceException("不支持的服务资源文件类型");
      }

    } catch (Exception e) {
      Debug.logError(e, "加载服务配置发生错误", module);
      throw new GenericServiceException(e);
    }
    return gobalLocalcontext;
  }


  public static void readFromZipFile(String zipFilePath, SAXParserFactory parserFactor,
          Map<String, ModelService> gobalLocalcontext) {
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(zipFilePath);
      Enumeration<? extends ZipEntry> e = zipFile.entries();
      while (e.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) e.nextElement();
        if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
          SAXParser parser = parserFactor.newSAXParser();
          ServiceSaxHandler saxHandler = new ServiceSaxHandler(gobalLocalcontext);
          parser.parse(zipFile.getInputStream(entry), saxHandler);
        } else {
          continue;
        }
      }

    } catch (IOException | ParserConfigurationException | SAXException e) {
      Debug.logError(e, "IOError :" + e.getMessage(), module);
    } finally {
      if (zipFile != null) try {
        zipFile.close();
      } catch (IOException e) {
        Debug.logError(e, "IOError :" + e.getMessage(), module);
      }
    }
  }

  /**
   * <p>
   * 同步执行服务
   * </p>
   * 
   * @param localName 要执行的服务名称
   * @param context 服务执行上下文<code>Map</code>对象
   * @return 如果执行成功，返回<code>Map</code>对象，否则，返回<code>null</code>
   */
  public Map<String, Object> runSync(String localName, Map<String, Object> context) {
    long begineTime = System.currentTimeMillis();
    Map<String, Object> result = null;
    ModelService modelService = null;
    GenericEngine engine = null;
    try {
      modelService = getLocalContext(localName);
      if (modelService == null) {
        Debug.logError("服务[" + localName + "]未找到", module);
        return ServiceUtils.returnError("服务[" + localName + "]未找到");
      }
      Boolean isExportApi = (Boolean) context.get(C.IS_EXPORT_API);
      if (isExportApi != null && isExportApi && !modelService.export) {
        Debug.logError("当前调用的服务[" + localName + "]为对外提供的服务，但是Export设置为了[" + modelService.export
                + "]", module);
        return ServiceUtils.returnError("当前调用的服务[" + localName + "]为对外提供的服务，但是Export设置为了["
                + modelService.export + "]");
      }

    } catch (GenericServiceException e) {
      Debug.logError(e, "获取服务[" + localName + "]发生异常:" + e.getMessage(), module);
      return ServiceUtils.returnError("ERROR");
    }

    try {
      engine = factory.getGenericEngine(modelService.engineName);
      if (engine == null) {
        Debug.logError("服务引擎[" + modelService.engineName + "]未找到", module);
        return ServiceUtils.returnError("ERROR");
      }
    } catch (GenericServiceException e) {
      Debug.logError(e, "获取服务引擎[" + modelService.engineName + "]发生异常:" + e.getMessage(), module);
      return ServiceUtils.returnError("ERROR");
    }


    ServiceEventActionInterface sea = null;
    if (CommUtils.isNotEmpty(modelService.sea)) {
      String seaName = modelService.sea.seaName;
      Class<?> seaClazz = null;
      try {
        // FIXME 可以将服务事件处理缓存起来
        seaClazz =
                Thread.currentThread().getContextClassLoader().loadClass(modelService.sea.seaName);
        sea = (ServiceEventActionInterface) seaClazz.newInstance();
      } catch (ClassNotFoundException e) {
        Debug.logError(e, "ServiceEventActionInterface[" + seaName + "]未找到:" + e.getMessage(),
                module);
        return ServiceUtils.returnError("ERROR");
      } catch (InstantiationException e) {
        Debug.logError(e, "ServiceEventActionInterface[" + seaName + "]实例化错误:" + e.getMessage(),
                module);
        return ServiceUtils.returnError("ERROR");
      } catch (IllegalAccessException e) {
        Debug.logError(e, "ServiceEventActionInterface[" + seaName + "]非法访问错误:" + e.getMessage(),
                module);
        return ServiceUtils.returnError("ERROR");
      }
    }

    if (modelService.persist && delegator == null) {
      Debug.logError("服务引擎需要执行持久化服务,但是Delegator为空", module);
      return ServiceUtils.returnError("ERROR");
    }
    
    // 是否使用事务
    boolean useTransaction =
            delegator != null && modelService.useTransaction && modelService.persist;
    //==========================================================================
    // 指定使用事务且不在测试模式时，开始一个事务;
    // 如果在测试下，不开启事务，哪怕服务显示指定使用事务,此时希望测试环境来接管事务管理。
    // 因为在有些测试环境下，不希望测试数据保存到数据库。
    //==========================================================================
    if (useTransaction && !isTestMode) { 
      delegator.beginTransaction();
    }

    try {
      context.put(C.CTX_SERVICE_DISPATCHER, this);
      // 将持久化服务对象添加至服务上下文
      if (modelService.persist) context.put(C.CTX_DELEGATOR, this.delegator);

      if (sea != null) {
        if (modelService.sea != null
                && ("all".equals(modelService.sea.triggerAt) || "before"
                        .equals(modelService.sea.triggerAt))) {
          sea.before(context);
        }
      }

      result = engine.runSync(localName, context);

      if (sea != null) {
        if (modelService.sea != null
                && ("all".equals(modelService.sea.triggerAt) || "success"
                        .equals(modelService.sea.triggerAt))) {
          sea.success(context, result);
        }
      }

      if (useTransaction && !isTestMode) {
        delegator.commitTransaction();
      }

      long costTime = System.currentTimeMillis() - begineTime;
      Debug.logInfo("调用服务[%s][%s][%s]耗时[%s]毫秒", module, localName, modelService.location,
              modelService.invoke, costTime);
      Debug.logDebug("" + result, module);
      return result;
    } catch (GenericServiceException e) {
      Debug.logError(e, "调用服务[%s][%s][%s]发生异常", module, localName, modelService.location,
              modelService.invoke);
      if (useTransaction && !isTestMode) {
        delegator.rollback();
      }
      return ServiceUtils.returnError("ERROR");
    } finally {
      if (useTransaction && !isTestMode) {
        delegator.endTransaction();
      }
    }
  }


  /**
   * <p>
   * 注册新的服务引擎，用于加入自定义实现的服务引擎。
   * </p>
   * 
   * @param engineName 要注册的服务引擎名称
   * @param engineClass 服务引擎实现类名称
   * @throws GenericServiceException
   */
  public void registerEngine(String engineName, String engineClass) throws GenericServiceException {
    if (factory != null) {
      try {
        if (factory.getGenericEngine(engineName) != null)
          throw new GenericServiceException("名为[" + engineName + "]的服务引擎已经存在");

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> c = loader.loadClass(engineClass);
        Constructor<GenericEngine> cn = CommUtils.cast(c.getConstructor(ServiceDispatcher.class));
        GenericEngine engine = cn.newInstance(this);
        factory.getEngines().put(engineName, engine);
      } catch (ClassNotFoundException cnfe) {
        throw new GenericServiceException("服务引擎实现类[" + engineClass + "]未找到", cnfe);
      } catch (NoSuchMethodException e) {
        throw new GenericServiceException("该方法未定义", e);
      } catch (SecurityException e) {
        throw new GenericServiceException("安全异常", e);
      } catch (InstantiationException e) {
        throw new GenericServiceException("实例化异常", e);
      } catch (IllegalAccessException e) {
        throw new GenericServiceException("非法访问异常", e);
      } catch (IllegalArgumentException e) {
        throw new GenericServiceException("非法参数异常", e);
      } catch (InvocationTargetException e) {
        throw new GenericServiceException("方法调用异常", e);
      }
    }
  }

  public void putModelService(ModelService modelService) {
    this.localContext.put(modelService.name, modelService);
  }

  public void removeModelService(String name) {
    this.localContext.remove(name);
  }

  public String getLocationOfGenericEngine(String engineName) throws GenericServiceException {
    GenericEngine ge = this.factory.getGenericEngine(engineName);
    if (ge != null) return ge.getClass().getName();
    return null;
  }

  public static class ServiceSaxHandler extends DefaultHandler {
    private ModelService modelService = null;
    private String content = null;
    private Map<String, ModelService> serviceMap = new HashMap<String, ModelService>();
    private Map<String, ModelService> gobalServiceMap;

    public ServiceSaxHandler(Map<String, ModelService> gobalServiceMap) {
      this.gobalServiceMap = gobalServiceMap;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      switch (qName) {
        case "service":
          modelService = new ModelService();
          modelService.name = attributes.getValue("name");
          modelService.engineName = attributes.getValue("engine");
          modelService.location = attributes.getValue("location");
          modelService.invoke = attributes.getValue("invoke");
          modelService.defaultEntityName = attributes.getValue("default-entity-name");
          modelService.auth =
                  Boolean.valueOf(attributes.getValue("auth") == null ? "false" : attributes
                          .getValue("auth"));
          modelService.export =
                  Boolean.valueOf(attributes.getValue("export") == null ? "false" : attributes
                          .getValue("export"));
          modelService.persist =
                  Boolean.valueOf(attributes.getValue("persist") == null ? "false" : attributes
                          .getValue("persist"));
          modelService.useTransaction =
                  Boolean.valueOf(attributes.getValue("use-transaction") == null
                          ? "false"
                          : attributes.getValue("use-transaction"));
          break;
        case "sea":
          ServiceEventAction sea = new ServiceEventAction();
          sea.seaName = attributes.getValue("name");
          sea.triggerAt = attributes.getValue("trigger-at");
          modelService.sea = sea;
          break;
        default:
          break;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

      switch (qName) {
        case "service":
          serviceMap.put(modelService.name, modelService);
          break;
        case "description":
          modelService.description = content;
          break;
        default:
          break;
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content = new String(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
      gobalServiceMap.putAll(serviceMap);
    }
  }


  public Delegator getDelegator() {
    return delegator;
  }

  public void setDelegator(Delegator delegator) {
    this.delegator = delegator;
  }


  public Map<String, ModelService> getLocalContext() {
    return localContext;
  }
  

  /**
   * <p>
   *    设置是否在测试模式下运行。如果是，则不使用事务。
   * </p>
   * @param isTestMode <code>true</code>是;<code>false</code>
   */
  public void setTestMode(boolean isTestMode) {
    this.isTestMode = isTestMode;
  }
}
