package org.huihoo.ofbiz.smart.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlException;

public class EntityConverter {
  
  private static final String[] INGORE_INCLUDED_NAME = {"_ctx","ebean","action.config."};
  
  private static final String TAG = EntityConverter.class.getName();
  
  private static final SimpleDateFormat FULL_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  private static final SimpleDateFormat SIMPLE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
  
  public static Object convertFrom(Class<?> convertClazz,Map<String,Object> ctx,Delegator delegator) {
    try {
      Object targetObj = convertClazz.newInstance();
      Map<?,?> onglCtx = Ognl.createDefaultContext(targetObj);    
      Ognl.setTypeConverter(onglCtx, entityTypeConvertor);
      Iterator<Entry<String, Object>> iter = ctx.entrySet().iterator();
      while (iter.hasNext()) {
        Entry<String, Object> entry = iter.next();
        String name = entry.getKey();
        if (isIngore(name)) {
          continue;
        }
        Object value = entry.getValue();
        try {
          if (name.indexOf(".") >= 0) {
            String realFieldName = name.substring(0, name.indexOf("."));
            Field field = targetObj.getClass().getDeclaredField(realFieldName);
            Annotation anno = field.getAnnotation(OneToOne.class);
            if (anno == null) {
              anno = field.getAnnotation(ManyToOne.class);
            }
            
            if (anno != null) {
              Object id = ctx.get(name);
              Log.d("[%s] has referenced model [#%s]", TAG,convertClazz,id);
              Object modelValue = delegator.findById(field.getType(), ctx.get(name));
              Ognl.setValue(realFieldName, onglCtx, targetObj, modelValue);
            }
            
          } else {
            Ognl.setValue(name, onglCtx, targetObj, value);
          }
        } catch (OgnlException e) {
          Log.w("Unable to get value of %s", TAG,name);
        } catch (NoSuchFieldException e) {
          Log.w("Unable to get value of %s", TAG,name);
        } catch (SecurityException e) {
          Log.w("Unable to get value of %s", TAG,name);
        } catch (GenericEntityException e) {
          Log.w("Unable to get value of %s", TAG,name);
        }
      }
      return targetObj;
    } catch (InstantiationException | IllegalAccessException e) {
      return null;
    }
    
  }
  
  private static boolean isIngore(String name) {
    for (String s : INGORE_INCLUDED_NAME) {
      if (name.indexOf(s) >= 0) {
        return true;
      }
    }
    return false;
  }
  
  @SuppressWarnings("rawtypes")
  private static DefaultTypeConverter entityTypeConvertor = new DefaultTypeConverter(){
    @Override
    public Object convertValue(Map context, Object value, Class toType) {
      Object result = null;
      if (toType == Date.class) {
        if (CommUtil.isEmpty(value)) {
          result = null;
        } else {
          String sValue = (String) value;
          try {
            if (sValue.length() > 10) {
              result = FULL_FORMATTER.parse(sValue);
            } else {
              result = SIMPLE_FORMATTER.parse(sValue);
            }
          } catch(ParseException e) {
            Log.w("The value[" + sValue +"] convert to Date failed.", TAG);
          }
        }
      } else if (toType == Timestamp.class && CommUtil.isNotEmpty(value)) {
        String sValue = (String) value;
        try {
          if (sValue.length() > 10) {
            result = new Timestamp(FULL_FORMATTER.parse(sValue).getTime());
          } else {
            result = new Timestamp(SIMPLE_FORMATTER.parse(sValue).getTime());
          }
        } catch(ParseException e) {
          Log.w("The value[" + sValue +"] convert to Timestamp failed.", TAG);
        }
      } else if ( (toType == Boolean.class || toType == boolean.class) && CommUtil.isNotEmpty(value)) {
        String sValue = (String) value;
        if ("Y".equalsIgnoreCase(sValue) || "YES".equalsIgnoreCase(sValue) 
                                            || "1".equalsIgnoreCase(sValue) || "TRUE".equalsIgnoreCase(sValue)) {
          result = Boolean.TRUE;
        } else {
          result = Boolean.FALSE;
        }
        
      } else if( (toType == Long.class || toType == long.class) ) {
        if (CommUtil.isNotEmpty(value)) {
          result = Long.valueOf("" + value);
        }
      } else if( (toType == Integer.class || toType == int.class) ) {
        if (CommUtil.isNotEmpty(value)) {
          result = Integer.valueOf("" + value);
        }
      } 
      else {
        result = super.convertValue(context, value, toType);
      }
      return result;
    }   
  };
}
