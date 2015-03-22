/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



public class CommUtils {
  private final static String module = CommUtils.class.getName();

  public static boolean isNumber(String input) {
    if (isEmpty(input)) return false;
    char[] cArrays = input.toCharArray();
    for (char c : cArrays) {
      if (!Character.isDefined(c)) return false;
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
      IllegalArgumentException e =
              new IllegalArgumentException(
                      "You must pass an even sized array to the toMap method (size = "
                              + data.length + ")");
      Debug.logError(e, "data size is illegal.", module);
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
      theBuf.append(entry.getValue());
      theBuf.append(System.getProperty("line.separator"));
    }
    return theBuf.toString();
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
          throw new IllegalArgumentException("Key(" + i + "), with value(" + entry.getKey()
                  + ") is not a " + keyType);
        }
        if (entry.getValue() != null && !valueType.isInstance(entry.getValue())) {
          throw new IllegalArgumentException("Value(" + i + "), with value(" + entry.getValue()
                  + ") is not a " + valueType);
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

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < byteData.length; i++) {
        String hex = Integer.toHexString(0xff & byteData[i]);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      return null;
    }

  }
}
