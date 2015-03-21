package org.huihoo.ofbiz.smart.base.utils;


import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormBeanUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FormBeanUtils.class);
  private static final String DATE = "yyyy-MM-dd";
  private static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
  private static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";

  static {
    ConvertUtils.register(new DateTimeConverter(), java.util.Date.class);
  }

  public static <E extends Object> E convertObjectFromMap(Class<E> clazz,
          Map<String, Object> fromMap) {
    if (clazz == null || fromMap == null) return null;

    E obj = null;
    try {
      obj = clazz.newInstance();
      BeanUtils.populate(obj, fromMap);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return obj;
  }

  public static Object populateObjectFromMap(Object obj, Map<String, Object> fromMap) {
    if (obj == null || fromMap == null) return null;

    try {
      BeanUtils.populate(obj, fromMap);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return obj;
  }



  public static class DateTimeConverter implements Converter {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Class<T> type, Object value) {
      if (CommUtils.isEmpty(value)) return null;
      if (value instanceof String) {
        String dateValue = value.toString().trim();
        int length = dateValue.length();
        if (type.equals(java.util.Date.class)) {
          try {
            SimpleDateFormat formatter = null;
            if (length <= 10) {
              formatter = new SimpleDateFormat(DATE, new DateFormatSymbols(Locale.CHINA));
              return (T) formatter.parse(dateValue);
            }
            if (length <= 19) {
              formatter = new SimpleDateFormat(DATETIME, new DateFormatSymbols(Locale.CHINA));
              return (T) formatter.parse(dateValue);
            }
            if (length <= 23) {
              formatter = new SimpleDateFormat(TIMESTAMP, new DateFormatSymbols(Locale.CHINA));
              return (T) formatter.parse(dateValue);
            }
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }
      }
      return (T) value;
    }

  }

}
