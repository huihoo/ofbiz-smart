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
package org.huihoo.ofbiz.smart.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



public class CommUtil {
  public final static Object[] EMPTY_ARGS = new Object[] {};
  public final static Class<?>[] EMPTY_CLAZZ = new Class<?>[] {};
  public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
  private final static String tag = CommUtil.class.getName();


  public static boolean isNumber(String input) {
    if (isEmpty(input)) return false;
    char[] cArrays = input.toCharArray();
    for (char c : cArrays) {
      if (!Character.isDigit(c)) return false;
    }
    return true;
  }



  @SuppressWarnings("unchecked")
  public static <V> V cast(Object object) {
    return (V) object;
  }

  public static boolean isNotEmpty(Object o) {
    return !isEmpty(o);
  }

  @SuppressWarnings("unchecked")
  public static boolean isEmpty(Object value) {
    if (value == null) return true;

    if (value instanceof String) return ((String) value).length() == 0;
    if (value instanceof Object[]) return ((Object[]) value).length == 0;
    if (value instanceof Collection) return ((Collection<? extends Object>) value).size() == 0;
    if (value instanceof Map) return ((Map<? extends Object, ? extends Object>) value).size() == 0;
    if (value instanceof CharSequence) return ((CharSequence) value).length() == 0;
    if (value instanceof Boolean) return false;
    if (value instanceof Number) return false;
    if (value instanceof Character) return false;
    if (value instanceof java.util.Date) return false;

    return false;
  }



  public static Map<String, Object> toMap(Object... data) {
    if (data.length == 1 && data[0] instanceof Map) {
      return checkMap(data[0]);
    }
    if (data.length % 2 == 1) {
      IllegalArgumentException e = new IllegalArgumentException(
              "You must pass an even sized array to the toMap method (size = " + data.length + ")");
      Log.e(e, "data size is illegal.", tag);
      throw e;
    }
    Map<String, Object> map = new HashMap<String, Object>();
    for (int i = 0; i < data.length;) {
      map.put((String) data[i++], data[i++]);
    }
    return map;
  }



  public static <K, V> String printMap(Map<? extends K, ? extends V> theMap) {
    StringBuilder theBuf = new StringBuilder();
    for (Map.Entry<? extends K, ? extends V> entry : theMap.entrySet()) {
      theBuf.append(entry.getKey());
      theBuf.append(" --> ");
      theBuf.append(entry.getValue() == null ? "" : entry.getValue());
      theBuf.append(System.getProperty("line.separator"));
    }
    return theBuf.toString();
  }

  
  private static <C extends Collection<?>> C checkCollectionCast(Object object, Class<C> clz) {
    return clz.cast(object);
  }


  @SuppressWarnings("unchecked")
  public static <T> List<T> checkList(Object object) {
    return (List<T>) checkCollectionCast(object, List.class);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> checkMap(Object object) {
    if (object != null && !(object instanceof Map)) throw new ClassCastException("Not a map");
    return (Map<K, V>) object;
  }

  public static <K, V> Map<K, V> checkMap(Object object, Class<K> keyType, Class<V> valueType) {
    if (object != null) {
      if (!(object instanceof Map<?, ?>)) throw new ClassCastException("Not a map");
      Map<?, ?> map = (Map<?, ?>) object;
      int i = 0;
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        if (entry.getKey() != null && !keyType.isInstance(entry.getKey())) {
          throw new IllegalArgumentException(
                  "Key(" + i + "), with value(" + entry.getKey() + ") is not a " + keyType);
        }
        if (entry.getValue() != null && !valueType.isInstance(entry.getValue())) {
          throw new IllegalArgumentException(
                  "Value(" + i + "), with value(" + entry.getValue() + ") is not a " + valueType);
        }
        i++;
      }
    }
    return checkMap(object);
  }

  public static String md5(String input) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(input.getBytes());
      byte byteData[] = md.digest();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < byteData.length; i++) {
        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }
      return toHexString(byteData);
    } catch (NoSuchAlgorithmException e) {
      Log.e(e, "Failed to generate MD5 : " + e.getMessage(), tag);
      return null;
    }
  }

  public static String hmacSha1(String data, String key) {
    try {
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
      Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
      mac.init(signingKey);
      byte[] rawHmac = mac.doFinal(data.getBytes());
      return toHexString(rawHmac);
    } catch (Exception e) {
      Log.e(e, "Failed to generate HMAC : " + e.getMessage(), tag);
      return null;
    }
  }

  public static String toHexString(byte[] byteData) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      String hex = Integer.toHexString(0xff & byteData[i]);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }



  /**
   * <p>
   * 将骆驼命名形式的字符串转换成下划线形式字符串
   * </p>
   * <p>
   * 比如： OrderHeader 转换后为: order_header
   * </p>
   * 
   * @param camelString 要转换的骆驼命名形式的字符串
   * @return 转换过后的字符串
   */
  public static String underscore(String camelString) {
    // 1.首先找到字符串中所有大写字母的索引位置并保存
    List<Integer> upperIdx = new ArrayList<Integer>();
    byte[] bytes = camelString.getBytes();
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      if (b >= 65 && b <= 90) {
        upperIdx.add(i);
      }
    }
    // 2.在大写字母之前插入下划线
    StringBuilder b = new StringBuilder(camelString);
    for (int i = upperIdx.size() - 1; i >= 0; i--) {
      Integer index = upperIdx.get(i);
      if (index != 0) {
        b.insert(index, "_");
      }
    }
    // 3.转换成小写并返回
    return b.toString().toLowerCase();
  }


}
