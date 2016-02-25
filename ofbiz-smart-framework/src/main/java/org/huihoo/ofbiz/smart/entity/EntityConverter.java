package org.huihoo.ofbiz.smart.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlException;

public class EntityConverter {

  private static final String[] INGORE_INCLUDED_NAME =
      {"_ctx", "ebean", "action.config.", "condition", "resultName", "orderBy", "error", "message", "andMap"};

  private static final String TAG = EntityConverter.class.getName();

  private static final SimpleDateFormat FULL_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final SimpleDateFormat YMDHM_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  private static final SimpleDateFormat SIMPLE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  public static void convertFrom(Object targetObj, Map<String, Object> ctx, Delegator delegator) {
    Map<?, ?> onglCtx = Ognl.createDefaultContext(targetObj);
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
            if (CommUtil.isNotEmpty(id)) {
              Log.d("[%s] has referenced model [#%s]", TAG, targetObj.getClass(), id);
              Object modelValue = delegator.findById(field.getType(), ctx.get(name));
              Ognl.setValue(realFieldName, onglCtx, targetObj, modelValue);
            }
          }

        } else if (name.endsWith("[]")) {
          Log.d("Collections detected name[%s] value[%s]", TAG, name, value);
          String realName = name.substring(0, name.length() - 2);
          Field collectionFiled = targetObj.getClass().getDeclaredField(realName);
          ParameterizedType type = (ParameterizedType) collectionFiled.getGenericType();
          
          Class<?> collectionTypeClazz = getTypeClass(type.getRawType(), 0);
          Class<?> genericTypeClazz = getGenericClass((ParameterizedType)type, 0);

          Log.d("type[%s] collectionTypeClazz[%s] genericTypeClazz[%s]", TAG, type, collectionTypeClazz, genericTypeClazz);
          
          Collection<Object> collections = null;
          if (collectionTypeClazz == List.class) {
            collections = new ArrayList<>();
          } else if (collectionTypeClazz == Set.class) {
            collections = new LinkedHashSet<>();
          }
          
          if (collections != null) {
            String[] values = null;
            if (value instanceof String[]) {
              values = (String[]) value;
            } else {
              values = new String[]{"" + value};
            }
            for (String v : values) {
              Object m = delegator.findById(genericTypeClazz, v);
              collections.add(m);
            }
          }
          Ognl.setValue(realName, onglCtx, targetObj, collections);
        } else {
          Ognl.setValue(name, onglCtx, targetObj, value);
        }
      } catch (OgnlException e) {
        Log.w("Unable to get value of %s", TAG, name);
      } catch (NoSuchFieldException e) {
        Log.w("NoSuchField,Unable to get value of %s", TAG, name);
      } catch (SecurityException e) {
        Log.w("Security,Unable to get value of %s", TAG, name);
      } catch (GenericEntityException e) {
        Log.w("Unable to get value of %s", TAG, name);
      }
    }
  }

  private static Class<?> getTypeClass(Type type, int i) {
    if (type instanceof ParameterizedType) {
      return getGenericClass((ParameterizedType) type, i);
    } else if (type instanceof TypeVariable) {
      return (Class<?>) getTypeClass(((TypeVariable<?>) type).getBounds()[0], 0);
    } else {
      return (Class<?>) type;
    }
  }

  private static Class<?> getGenericClass(ParameterizedType parameterizedType, int i) {
    Object genericClass = parameterizedType.getActualTypeArguments()[i];
    if (genericClass instanceof ParameterizedType) { 
      return (Class<?>) ((ParameterizedType) genericClass).getRawType();
    } else if (genericClass instanceof GenericArrayType) { 
      return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
    } else if (genericClass instanceof TypeVariable) { 
      return (Class<?>) getTypeClass(((TypeVariable<?>) genericClass).getBounds()[0], 0);
    } else {
      return (Class<?>) genericClass;
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
  private static DefaultTypeConverter entityTypeConvertor = new DefaultTypeConverter() {
    @Override
    public Object convertValue(Map context, Object value, Class toType) {
      Log.i("toType[%s] value[%s]", TAG, toType, value);
      Object result = null;
      if (toType == Date.class) {
        if (CommUtil.isEmpty(value)) {
          result = null;
        } else {
          String sValue = (String) value;
          try {
            if (sValue.length() == 19) {
              result = FULL_FORMATTER.parse(sValue);
            } else if (sValue.length() == 16) {
              result = YMDHM_FORMATTER.parse(sValue);
            } else {
              result = SIMPLE_FORMATTER.parse(sValue);
            }
          } catch (ParseException e) {
            Log.w("The value[" + sValue + "] convert to Date failed.", TAG);
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
        } catch (ParseException e) {
          Log.w("The value[" + sValue + "] convert to Timestamp failed.", TAG);
        }
      } else if ((toType == Boolean.class || toType == boolean.class) && CommUtil.isNotEmpty(value)) {
        String sValue = (String) value;
        if ("Y".equalsIgnoreCase(sValue) || "YES".equalsIgnoreCase(sValue) || "1".equalsIgnoreCase(sValue)
            || "TRUE".equalsIgnoreCase(sValue)) {
          result = Boolean.TRUE;
        } else {
          result = Boolean.FALSE;
        }
      } else if ((toType == Long.class || toType == long.class)) {
        if (CommUtil.isNotEmpty(value)) {
          result = Long.parseLong("" + value);
        }
      } else if ((toType == Integer.class || toType == int.class)) {
        if (CommUtil.isNotEmpty(value)) {
          result = Integer.parseInt("" + value);
        }
      } else if ((toType == Double.class || toType == double.class)) {
        if (CommUtil.isNotEmpty(value)) {
          result = Double.parseDouble("" + value);
        }
      } else if ((toType == Float.class || toType == float.class)) {
        if (CommUtil.isNotEmpty(value)) {
          result = Float.parseFloat("" + value);
        }
      } else if ((toType == BigDecimal.class)) {
        if (CommUtil.isNotEmpty(value)) {
          result = new BigDecimal("" + value);
        }
      } else {
        result = super.convertValue(context, value, toType);
      }
      return result;
    }
  };
}
