package org.huihoo.ofbiz.smart.service.engine;




import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintViolation;
import org.huihoo.ofbiz.smart.base.validation.ValidateProfile;
import org.huihoo.ofbiz.smart.base.validation.Validator;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EntityConverter;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;


public class EntityAutoEngine extends GenericAsyncEngine {
  private final static String TAG = EntityAutoEngine.class.getName();

  private final static Map<String, Class<?>> ENGITY_CLAZZ_MAP = new ConcurrentHashMap<>();

  public EntityAutoEngine(ServiceDispatcher serviceDispatcher) {
    super(serviceDispatcher);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
    if (CommUtil.isEmpty(serviceName)) {
      throw new GenericServiceException("The serviceName is empty.");
    }
    if (ctx == null) {
      throw new GenericServiceException("The service context is null.");
    }

    ServiceModel serviceModel = serviceDispatcher.getServiceContextMap().get(serviceName);
    if (serviceModel == null) {
      throw new GenericServiceException("Unable to locate the service [" + serviceName + "]");
    }

    if (CommUtil.isEmpty(serviceModel.invoke) || CommUtil.isEmpty(serviceModel.engineName)) {
      throw new GenericServiceException("The service [" + serviceName + "] has not been set invoke or engineName");
    }

    Delegator delegator = ServiceUtil.getDelegator(ctx);
    if (delegator == null) {
      throw new GenericServiceException("Service [" + serviceName + "] required to set Delegator.");
    }

    Class<?> entityClazz = ENGITY_CLAZZ_MAP.get(serviceModel.entityName);
    if (entityClazz == null) {
      try {
        entityClazz = Thread.currentThread().getContextClassLoader().loadClass(serviceModel.entityName);
        ENGITY_CLAZZ_MAP.put(serviceModel.entityName, entityClazz);
      } catch (ClassNotFoundException e) {
        throw new GenericServiceException("EngityClass [" + serviceModel.entityName + "] not found.");
      }
    }
    try {
      Boolean useCache = (Boolean) ctx.get(C.ENTITY_USE_CACHE);
      Integer liveTimeInSeconds = (Integer) ctx.get(C.ENTITY_LIVETIMEIN_SECONDS);
      if (useCache == null) {
        useCache = Boolean.FALSE;
      }
      if (liveTimeInSeconds == null) {
        liveTimeInSeconds = 0;
      }
      
      Map<String,Object> successResult = ServiceUtil.returnSuccess();
      String resultName = (String) ctx.get(C.SERVICE_RESULT_NAME_ATTRIBUTE);
      String condition = (String) ctx.get(C.ENTITY_CONDTION);
      Map<String, Object> andMap = (Map<String, Object>) ctx.get(C.ENTITY_ANDMAP);
      Set<String> fieldsToSelect = (Set<String>) ctx.get(C.ENTITY_FIELDS_TO_SELECT);
      List<String> orderBy = (List<String>) ctx.get(C.ENTITY_ORDERBY);
       
      
      switch (serviceModel.invoke) {
        case C.SERVICE_ENGITYAUTO_CREATE:
          Object modelObj = entityClazz.newInstance();
          EntityConverter.convertFrom(modelObj, ctx, delegator);
          Map<String,List<ConstraintViolation>> constraintViolationMap =  Validator.validate(modelObj, ValidateProfile.CREATE);
          if (CommUtil.isNotEmpty(constraintViolationMap)) {
            return ServiceUtil.returnProplem("VALIDATION_NOT_PASSED", "Validation has been not passed.",constraintViolationMap);
          }
          delegator.save(modelObj);  
          if (resultName != null) {
            successResult.put(resultName, modelObj);
          } else {
            successResult.put(C.ENTITY_MODEL_NAME, modelObj);
          }
          successResult.put(C.FLASH_SCOPE_SUCCESS_FLAG_ATTRIBUTE, true); //XXX 成功标志
          break;
        case C.SERVICE_ENGITYAUTO_UPDATE:
          Object id = ctx.get(C.ENTITY_ID_NAME);
          if (CommUtil.isEmpty(id)) {
            return ServiceUtil.returnProplem("ENTITY_ID_REQUIRED","The entity id required.");
          }
          Object obj = delegator.findById(entityClazz, id,useCache,liveTimeInSeconds);
          if (obj != null) {
            EntityConverter.convertFrom(obj, ctx, delegator);
            delegator.save(obj); 

            if (resultName != null) {
              successResult.put(resultName, obj);
            } else {
              successResult.put(C.ENTITY_MODEL_NAME, obj);
            }
            successResult.put(C.FLASH_SCOPE_SUCCESS_FLAG_ATTRIBUTE, true); //XXX 成功标志
          }
          break;
        case C.SERVICE_ENGITYAUTO_REMOVE:
          id = ctx.get(C.ENTITY_ID_NAME);
          if (CommUtil.isEmpty(id)) {
              return ServiceUtil.returnProplem("ENTITY_ID_REQUIRED","The entity id required.");
          }
          obj = delegator.findById(entityClazz, id);
          if (obj != null) {
            try {
              Object removedObj = obj;
              delegator.remove(obj);
              successResult.put(C.ENTITY_REMOVED_NAME, removedObj);
              successResult.put(C.FLASH_SCOPE_SUCCESS_FLAG_ATTRIBUTE, true); //XXX 成功标志
            } catch(GenericEntityException e) {
              if (e.getMessage() != null && e.getMessage().indexOf("CONSTRAINT") != -1) {
                return ServiceUtil.returnProplem("ENTITY_REFERENCED_CONSTRAINT", "The entity has referenced another entity.");
              }
            }
          }
          break;
        case C.SERVICE_ENGITYAUTO_FINDBYID:
          id = ctx.get(C.ENTITY_ID_NAME);
          if (CommUtil.isEmpty(id)) {
            id = ( andMap == null ? null : andMap.get(C.ENTITY_ID_NAME) );
            if (CommUtil.isEmpty(id)) {
              return ServiceUtil.returnProplem("ENTITY_ID_REQUIRED","The entity id required.");
            }
          }
          obj = delegator.findById(entityClazz, id,useCache);
          if (resultName != null) {
            successResult.put(resultName, obj);
          } else {
            successResult.put(C.ENTITY_MODEL_NAME, obj);
          }
          break;
        case C.SERVICE_ENGITYAUTO_FINDUNIQUEBYAND:
          andMap = (Map<String, Object>) ctx.get(C.ENTITY_ANDMAP);
          fieldsToSelect = (Set<String>) ctx.get(C.ENTITY_FIELDS_TO_SELECT);
          obj = delegator.findUniqueByAnd(entityClazz, andMap,fieldsToSelect,useCache,liveTimeInSeconds);
          if (resultName != null) {
            successResult.put(resultName, obj);
          } else {
            successResult.put(C.ENTITY_MODEL_NAME, obj);
          }
          break;
        case C.SERVICE_ENGITYAUTO_FINDLISTBYAND:
        case C.SERVICE_ENGITYAUTO_FINDLISTBYCOND:
          if (orderBy == null && hasUpdatedAtField(entityClazz)) {
            orderBy = Arrays.asList(new String[]{C.ENTITY_ORDERBY_DEFAULT_FIELD});
          }
          List<?> pList = null;
          if (CommUtil.isNotEmpty(condition)) {
            pList = delegator.findListByCond(entityClazz, condition, fieldsToSelect, orderBy, useCache,liveTimeInSeconds);
          } else {
            pList = delegator.findListByAnd(entityClazz, andMap, fieldsToSelect, orderBy,liveTimeInSeconds);
          }
          if (resultName != null) {
            successResult.put(resultName, pList);
          } else {
            successResult.put(C.ENTITY_MODEL_LIST, pList);
          }
          break;
        case C.SERVICE_ENGITYAUTO_FINDPAGEBYAND:
        case C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND:
          condition = (String) ctx.get(C.ENTITY_CONDTION);
          andMap = (Map<String, Object>) ctx.get(C.ENTITY_ANDMAP);
          fieldsToSelect = (Set<String>) ctx.get(C.ENTITY_FIELDS_TO_SELECT);
          orderBy = (List<String>) ctx.get(C.ENTITY_ORDERBY);
          if (orderBy == null && hasUpdatedAtField(entityClazz)) {
            orderBy = Arrays.asList(new String[]{C.ENTITY_ORDERBY_DEFAULT_FIELD});
          }
          Integer pageNo = Integer.parseInt((ctx.get(C.PAGE_PAGE_NO) == null ? 1 : ctx.get(C.PAGE_PAGE_NO)) + "");
          Integer pageSize = Integer.parseInt((ctx.get(C.PAGE_PAGE_SIZE) == null ? 20 : ctx.get(C.PAGE_PAGE_SIZE)) + "");
          
          Map<String, Object> pMap = null;
          if (CommUtil.isNotEmpty(condition)) {
            pMap = delegator.findPageByCond(entityClazz, condition, pageNo, pageSize, fieldsToSelect, orderBy, useCache,liveTimeInSeconds);
          } else {
            pMap = delegator.findPageByAnd(entityClazz, andMap, pageNo, pageSize, fieldsToSelect, orderBy, useCache,liveTimeInSeconds);
          }
          if (CommUtil.isNotEmpty(resultName)) {
            successResult.put(resultName, pMap);
          } else {
            successResult.putAll(pMap);
          }
          break;
        default:
          //Ingore..
          break;
      }
      return successResult;
    } catch (Exception e) {
      Log.e(e, "EntityAutoEngine has an exception.", TAG);
      return ServiceUtil.returnProplem("ENTITY_AUTO_ENGINE_ERROR", e.getMessage());
    }
  }

  @Override
  public String getName() {
    return "entityAuto";
  }
  
  
  
  //=============================================================
  // Private Method
  //=============================================================
  
  
  private static boolean hasUpdatedAtField(Class<?> entityClazz) {
    if (entityClazz == null) {
      return false;
    }
    boolean flag = true;
    try {
      entityClazz.getDeclaredField(C.ENTITY_UPDATED_AT);
    } catch (NoSuchFieldException e) {
      flag = false;
    } catch (SecurityException e) {
      flag = false;
    }
    
    if (flag) {
      return true;
    } else {
      Class<?> superClazz = null;
      if (!flag) {
        superClazz = entityClazz.getSuperclass();
        flag = hasUpdatedAtField(superClazz);
      }
      
      if (!flag && superClazz != null) {
        flag = hasUpdatedAtField(superClazz.getSuperclass());
      }
    }
    return flag;
  }
  
}
