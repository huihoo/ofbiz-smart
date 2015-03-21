package org.huihoo.ofbiz.smart.service.engine;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.FormBeanUtils;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ModelService;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;




public class SimpleServiceEngine extends GenericAsyncEngine {
  private static final String module = SimpleServiceEngine.class.getName();

  public SimpleServiceEngine(ServiceDispatcher dispatcher) {
    super(dispatcher);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException {
    if (localName == null) {
      Debug.logError("本地服务名称为空",module);
      throw new GenericServiceException("本地服务名称为空");
    }

    if (context == null) {
      Debug.logError("服务参数上下文为空",module);
      throw new GenericServiceException("服务参数上下文为空");
    }

    ModelService modelService = this.dispatcher.getLocalContext(localName);
    if (modelService == null) {
      Debug.logError("名为[" + localName + "]的服务模型为空",module);
      throw new GenericServiceException("名为[" + localName + "]的服务模型为空");
    }

    if (modelService.invoke == null || modelService.defaultEntityName == null) {
      throw new GenericServiceException("服务 [" + modelService.name
              + "] 缺失 invoke 或 default-entity-name 参数");
    }

    Delegator delegator = (Delegator) context.get(C.CTX_DELEGATOR);
    if (delegator == null) {
      throw new GenericServiceException("服务[" + localName + "]需要Delegator");
    }

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> entityClazz = null;
    try {
      entityClazz = cl.loadClass(modelService.defaultEntityName);
    } catch (ClassNotFoundException e) {
      throw new GenericServiceException("实体[" + modelService.defaultEntityName + "]未找到");
    }
    try {
      boolean useCache = false;
      Map<String, Object> success = ServiceUtils.returnSuccess();
      String defaultEntityName = modelService.defaultEntityName;
      if ("create".equals(modelService.invoke)) {
        Object newModel = FormBeanUtils.convertObjectFromMap(entityClazz, context);
        copyModelTypeProperty(newModel, context, delegator);
        delegator.save(newModel);
        success.put(C.TIP_FLASH_SUCCESS, true);
        success.put("model", newModel);
      } else if ("update".equals(modelService.invoke)) {
        Object id = context.get("id");
        if (CommUtils.isEmpty(id)) {
          return ServiceUtils.returnError("需要设置实体对象主键的值");
        }
        Object obj = delegator.findById(defaultEntityName, id, useCache);
        obj = FormBeanUtils.populateObjectFromMap(obj, context);
        copyModelTypeProperty(obj, context, delegator);
        delegator.save(obj);
        success.put(C.TIP_FLASH_SUCCESS, true);
        success.put("model", obj);
      } else if ("findById".equals(modelService.invoke)) {
        Object id = context.get("id");
        if (CommUtils.isEmpty(id)) {
          return ServiceUtils.returnError("需要设置实体对象主键的值");
        }
        Object obj = delegator.findById(defaultEntityName, id, useCache);
        success.put("model", obj);
      } else if ("remove".equals(modelService.invoke)) {
        Object id = context.get("id");
        if (CommUtils.isEmpty(id)) {
          return ServiceUtils.returnError("需要设置实体对象主键的值");
        }
        Object obj = delegator.findById(defaultEntityName, id, useCache);
        if (obj != null) {
          Object removedObj = obj;
          delegator.remove(obj);
          success.put("removed", removedObj);
        }
      } else if ("findByAnd".equals(modelService.invoke)) {
        Map<String, Object> fields = (Map<String, Object>) context.get("fields");
        List<String> orderBy = (List<String>) context.get("orderBy");
        if(CommUtils.isEmpty(orderBy)){
          orderBy = new ArrayList<String>();
          orderBy.add("updatedAt desc");
        }
        List<?> enties = delegator.findByAnd(defaultEntityName, fields, orderBy, useCache);
        success.put("enties", enties);
      } else if ("findPageByAnd".equals(modelService.invoke)
              || "findPageByCondition".equals(modelService.invoke)) {
        String condition = (String) context.get("condition");
        Map<String, Object> fields = (Map<String, Object>) context.get("fields");
        Set<String> fieldsToSelect = (Set<String>) context.get("fieldsToSelect");
        List<String> orderBy = (List<String>) context.get("orderBy");
        if(CommUtils.isEmpty(orderBy)){
          orderBy = new ArrayList<String>();
          orderBy.add("updatedAt desc");
        }
        Integer pageNo = (Integer) context.get("pageNo");
        Integer pageSize = (Integer) context.get("pageSize");
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 20;
        Map<String, Object> pMap = null;
        if (CommUtils.isNotEmpty(condition))
          pMap =
                  delegator.findPageByCondition(defaultEntityName, condition, fieldsToSelect,
                          orderBy, pageNo, pageSize, useCache);
        else
          pMap =
                  delegator.findPageByAnd(defaultEntityName, fields, fieldsToSelect, orderBy,
                          pageNo, pageSize, useCache);
        success.putAll(pMap);
      } else {
        return ServiceUtils.returnError("不支持的[" + modelService.invoke + "]");
      }
      return success;
    } catch (GenericEntityException e) {
      Debug.logError(e,e.getMessage(), module);
      Map<String, Object> errorMap = ServiceUtils.returnError(e.getMessage());
      errorMap.put(C.TIP_FLASH_ERROR, e.getMessage());
      return errorMap;
    }
  }

  /**
   * <p>
   * 设置字段类型为<code>com.avaje.ebean.Model</code>的属性;
   * </p>
   * <p>
   *    
   * </p>
   * 
   * @param obj 要设置的对象
   * @param context 服务上下文
   * @param delegator
   */
  private static void copyModelTypeProperty(Object obj, Map<String, Object> context,
          Delegator delegator) {
    Set<Entry<String, Object>> cSet = context.entrySet();
    Iterator<Entry<String, Object>> cSetIter = cSet.iterator();
    while (cSetIter.hasNext()) {
      Map.Entry<String, Object> cEntry = cSetIter.next();
      String cName = cEntry.getKey();
      int fDotIdx = cName.indexOf(".");
      if (fDotIdx != -1) {
        String pName = cName.substring(0, fDotIdx);
        Class<?> cTypeClazz = null;
        try {
          String fName = cName.substring(0, fDotIdx);
          cTypeClazz = PropertyUtils.getPropertyType(obj, fName);
          //如果是集合类型，获取集合里的对象类型
          if(cTypeClazz.getName().endsWith("List")){  
             Field f = obj.getClass().getDeclaredField(fName);
             //java.util.List<?>
             String fTypeName = f.getGenericType().toString();
             String targetClazzName = fTypeName.substring(fTypeName.indexOf("<")+1,fTypeName.indexOf(">"));
             List<Object> objList = new ArrayList<>();
             String ppName = cName.substring(fDotIdx + 1);
             Object cEntryValue = cEntry.getValue();
             
             Object pValue = null;
             //如果是数组
             if(cEntryValue instanceof String[]){
               String[] cEntryValueArry = (String[])cEntryValue;
               StringBuffer sb = new StringBuffer();
               for(int i = 0; i < cEntryValueArry.length; i++){
                 sb.append(cEntryValueArry[i]);
                 if(i < cEntryValueArry.length -1)
                   sb.append("#");
               }
               //拼接条件字符串
               String condition = ppName+"@in@"+sb.toString();
               pValue = delegator.findList(targetClazzName, condition, null, null, false);
               objList.addAll((List<?>)pValue);
             }else{
               Map<String, Object> fields = CommUtils.toMap(ppName, cEntryValue);
               pValue = delegator.findUniqueByAnd(targetClazzName, fields, false);
               objList.add(pValue);
             }
             BeanUtils.copyProperty(obj, pName, objList);
          }else{
            String ppName = cName.substring(fDotIdx + 1);
            Map<String, Object> fields = CommUtils.toMap(ppName, cEntry.getValue());
            Object pValue = delegator.findUniqueByAnd(cTypeClazz.getName(), fields, false);
            BeanUtils.copyProperty(obj, pName, pValue);
          }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | GenericEntityException | NoSuchFieldException | SecurityException e) {
          Debug.logError(e,"设置属性[%s][%s]发生异常", module,cTypeClazz,cName);
        }
      }
    }
  }

}
