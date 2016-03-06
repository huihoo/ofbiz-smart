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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



public class CommUtil {
  public static final Object[] EMPTY_ARGS = new Object[] {};
  public static final Class<?>[] EMPTY_CLAZZ = new Class<?>[] {};
  public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
  public static final String TAG = CommUtil.class.getName();
  public static final int INDEX_NOT_FOUND = -1;
  public static final int NOT_FOUND = -1;

  private final static ThreadLocal<SimpleDateFormat> yyyyMMddFormater = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat("yyyy-MM-dd");
    }
  };

  public static boolean isNumber(String input) {
    if (isEmpty(input)) return false;
    char[] cArrays = input.toCharArray();
    for (char c : cArrays) {
      if (!Character.isDigit(c)) return false;
    }
    return true;
  }



  public static String stripXSS(String value) {
    if (value != null) {
      // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
      // avoid encoded attacks.
      // value = ESAPI.encoder().canonicalize(value);
      // Avoid null characters
      value = value.replaceAll("", "");
      // Avoid anything between script tags
      Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
      value = scriptPattern.matcher(value).replaceAll("");
      
      // FIXME img src='' REQUIRED ???
      
      // Avoid anything in a src="http://www.yihaomen.com/article/java/..." type of e­xpression
      //scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
      //    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      //value = scriptPattern.matcher(value).replaceAll("");
      //scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
      //    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      //value = scriptPattern.matcher(value).replaceAll("");
      
      // Remove any lonesome </script> tag
      scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
      value = scriptPattern.matcher(value).replaceAll("");
      // Remove any lonesome <script ...> tag
      scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      value = scriptPattern.matcher(value).replaceAll("");
      // Avoid eval(...) e­xpressions
      scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      value = scriptPattern.matcher(value).replaceAll("");
      // Avoid e­xpression(...) e­xpressions
      scriptPattern =
          Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      value = scriptPattern.matcher(value).replaceAll("");
      // Avoid javascript:... e­xpressions
      scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
      value = scriptPattern.matcher(value).replaceAll("");
      // Avoid vbscript:... e­xpressions
      scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
      value = scriptPattern.matcher(value).replaceAll("");
      // Avoid onload= e­xpressions
      scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      value = scriptPattern.matcher(value).replaceAll("");
    }
    return value;
  }


  public static String[] stripXSS(String[] value) {
    if (value != null) {
      String[] newInput = new String[value.length];
      for (int i = 0; i < newInput.length; i++) {
        newInput[i] = stripXSS(value[i]);
      }
      return newInput;
    }
    return null;
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
      Log.e(e, "data size is illegal.", TAG);
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
          throw new IllegalArgumentException("Key(" + i + "), with value(" + entry.getKey() + ") is not a " + keyType);
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
      Log.e(e, "Failed to generate MD5 : " + e.getMessage(), TAG);
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
      Log.e(e, "Failed to generate HMAC : " + e.getMessage(), TAG);
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

  public static Map<String, Object> convertUnderscoreKeyMapToCamelKeyMap(Map<String, Object> map) {
    Map<String, Object> newMap = new HashMap<String, Object>();
    Set<String> keys = map.keySet();
    Iterator<String> kIter = keys.iterator();
    while (kIter.hasNext()) {
      String key = kIter.next();
      if (key.indexOf("_") != -1) {
        StringTokenizer st = new StringTokenizer(key, "_");
        StringBuilder newKey = new StringBuilder();
        int j = 0;
        while (st.hasMoreElements()) {
          String k = st.nextToken();
          if (j == 0) {
            newKey.append(k);
          } else {
            newKey.append(k.substring(0, 1).toUpperCase() + k.substring(1));
          }
          j++;
        }
        newMap.put(newKey.toString(), map.get(key));
      } else {
        newMap.put(key, map.get(key));
      }
    }
    return newMap;
  }

  public static String underscore(String camelString) {

    List<Integer> upperIdx = new ArrayList<Integer>();
    byte[] bytes = camelString.getBytes();
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      if (b >= 65 && b <= 90) {
        upperIdx.add(i);
      }
    }

    StringBuilder b = new StringBuilder(camelString);
    for (int i = upperIdx.size() - 1; i >= 0; i--) {
      Integer index = upperIdx.get(i);
      if (index != 0) {
        b.insert(index, "_");
      }
    }

    return b.toString().toLowerCase();
  }


  // ================================================================================
  // Copy from org.springframework.util
  // ================================================================================
  public static String[] tokenizeToStringArray(String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }


  public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {

    if (str == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }


  public static String[] toStringArray(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  public static boolean hasLength(CharSequence str) {
    return (str != null && str.length() > 0);
  }

  public static boolean hasLength(String str) {
    return hasLength((CharSequence) str);
  }

  public static boolean hasText(CharSequence str) {
    if (!hasLength(str)) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }



  public static String friendlyTime(long time) {
    String ftime = "";
    Calendar cal = Calendar.getInstance();

    // 判断是否是同一天
    String curDate = yyyyMMddFormater.get().format(cal.getTime());
    String paramDate = yyyyMMddFormater.get().format(time);
    if (curDate.equals(paramDate)) {
      int hour = (int) ((cal.getTimeInMillis() - time) / 3600000);
      if (hour == 0)
        ftime = Math.max((cal.getTimeInMillis() - time) / 60000, 1) + "分钟前";
      else
        ftime = hour + "小时前";
      return ftime;
    }

    long lt = time / 86400000;
    long ct = cal.getTimeInMillis() / 86400000;
    int days = (int) (ct - lt);
    if (days == 0) {
      int hour = (int) ((cal.getTimeInMillis() - time) / 3600000);
      if (hour == 0)
        ftime = Math.max((cal.getTimeInMillis() - time) / 60000, 1) + "分钟前";
      else
        ftime = hour + "小时前";
    } else if (days == 1) {
      ftime = "昨天";
    } else if (days == 2) {
      ftime = "前天 ";
    } else if (days > 2 && days < 31) {
      ftime = days + "天前";
    } else if (days >= 31 && days <= 2 * 31) {
      ftime = "一个月前";
    } else if (days > 2 * 31 && days <= 3 * 31) {
      ftime = "2个月前";
    } else if (days > 3 * 31 && days <= 4 * 31) {
      ftime = "3个月前";
    } else {
      ftime = yyyyMMddFormater.get().format(time);
    }
    return ftime;
  }

  // ===============================================================================
  // copy from commons-lang3
  // ===============================================================================
  /**
   * <p>
   * Checks that the CharSequence does not contain certain characters.
   * </p>
   *
   * <p>
   * A {@code null} CharSequence will return {@code true}. A {@code null} invalid character array
   * will return {@code true}. An empty CharSequence (length()=0) always returns true.
   * </p>
   *
   * <pre>
   * StringUtils.containsNone(null, *)       = true
   * StringUtils.containsNone(*, null)       = true
   * StringUtils.containsNone("", *)         = true
   * StringUtils.containsNone("ab", '')      = true
   * StringUtils.containsNone("abab", 'xyz') = true
   * StringUtils.containsNone("ab1", 'xyz')  = true
   * StringUtils.containsNone("abz", 'xyz')  = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @param searchChars an array of invalid chars, may be null
   * @return true if it contains none of the invalid chars, or is null
   * @since 2.0
   * @since 3.0 Changed signature from containsNone(String, char[]) to containsNone(CharSequence,
   *        char...)
   */
  public static boolean containsNone(final CharSequence cs, final char... searchChars) {
    if (cs == null || searchChars == null) {
      return true;
    }
    final int csLen = cs.length();
    final int csLast = csLen - 1;
    final int searchLen = searchChars.length;
    final int searchLast = searchLen - 1;
    for (int i = 0; i < csLen; i++) {
      final char ch = cs.charAt(i);
      for (int j = 0; j < searchLen; j++) {
        if (searchChars[j] == ch) {
          if (Character.isHighSurrogate(ch)) {
            if (j == searchLast) {
              // missing low surrogate, fine, like String.indexOf(String)
              return false;
            }
            if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
              return false;
            }
          } else {
            // ch is in the Basic Multilingual Plane
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * <p>
   * Replaces all occurrences of a String within another String.
   * </p>
   *
   * <p>
   * A {@code null} reference passed to this method is a no-op.
   * </p>
   *
   * <pre>
   * StringUtils.replace(null, *, *)        = null
   * StringUtils.replace("", *, *)          = ""
   * StringUtils.replace("any", null, *)    = "any"
   * StringUtils.replace("any", *, null)    = "any"
   * StringUtils.replace("any", "", *)      = "any"
   * StringUtils.replace("aba", "a", null)  = "aba"
   * StringUtils.replace("aba", "a", "")    = "b"
   * StringUtils.replace("aba", "a", "z")   = "zbz"
   * </pre>
   *
   * @see #replace(String text, String searchString, String replacement, int max)
   * @param text text to search and replace in, may be null
   * @param searchString the String to search for, may be null
   * @param replacement the String to replace it with, may be null
   * @return the text with any replacements processed, {@code null} if null String input
   */
  public static String replace(final String text, final String searchString, final String replacement) {
    return replace(text, searchString, replacement, -1);
  }

  /**
   * <p>
   * Replaces a String with another String inside a larger String, for the first {@code max} values
   * of the search String.
   * </p>
   *
   * <p>
   * A {@code null} reference passed to this method is a no-op.
   * </p>
   *
   * <pre>
   * StringUtils.replace(null, *, *, *)         = null
   * StringUtils.replace("", *, *, *)           = ""
   * StringUtils.replace("any", null, *, *)     = "any"
   * StringUtils.replace("any", *, null, *)     = "any"
   * StringUtils.replace("any", "", *, *)       = "any"
   * StringUtils.replace("any", *, *, 0)        = "any"
   * StringUtils.replace("abaa", "a", null, -1) = "abaa"
   * StringUtils.replace("abaa", "a", "", -1)   = "b"
   * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
   * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
   * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
   * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
   * </pre>
   *
   * @param text text to search and replace in, may be null
   * @param searchString the String to search for, may be null
   * @param replacement the String to replace it with, may be null
   * @param max maximum number of values to replace, or {@code -1} if no maximum
   * @return the text with any replacements processed, {@code null} if null String input
   */
  public static String replace(final String text, final String searchString, final String replacement, int max) {
    if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
      return text;
    }
    int start = 0;
    int end = text.indexOf(searchString, start);
    if (end == INDEX_NOT_FOUND) {
      return text;
    }
    final int replLength = searchString.length();
    int increase = replacement.length() - replLength;
    increase = increase < 0 ? 0 : increase;
    increase *= max < 0 ? 16 : max > 64 ? 64 : max;
    final StringBuilder buf = new StringBuilder(text.length() + increase);
    while (end != INDEX_NOT_FOUND) {
      buf.append(text.substring(start, end)).append(replacement);
      start = end + replLength;
      if (--max == 0) {
        break;
      }
      end = text.indexOf(searchString, start);
    }
    buf.append(text.substring(start));
    return buf.toString();
  }


  /**
   * <p>
   * Checks if the CharSequence contains any character in the given set of characters.
   * </p>
   *
   * <p>
   * A {@code null} CharSequence will return {@code false}. A {@code null} or zero length search
   * array will return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.containsAny(null, *)                = false
   * StringUtils.containsAny("", *)                  = false
   * StringUtils.containsAny(*, null)                = false
   * StringUtils.containsAny(*, [])                  = false
   * StringUtils.containsAny("zzabyycdxx",['z','a']) = true
   * StringUtils.containsAny("zzabyycdxx",['b','y']) = true
   * StringUtils.containsAny("aba", ['z'])           = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @param searchChars the chars to search for, may be null
   * @return the {@code true} if any of the chars are found, {@code false} if no match or null input
   * @since 2.4
   * @since 3.0 Changed signature from containsAny(String, char[]) to containsAny(CharSequence,
   *        char...)
   */
  public static boolean containsAny(final CharSequence cs, final char... searchChars) {
    if (isEmpty(cs) || isEmpty(searchChars)) {
      return false;
    }
    final int csLength = cs.length();
    final int searchLength = searchChars.length;
    final int csLast = csLength - 1;
    final int searchLast = searchLength - 1;
    for (int i = 0; i < csLength; i++) {
      final char ch = cs.charAt(i);
      for (int j = 0; j < searchLength; j++) {
        if (searchChars[j] == ch) {
          if (Character.isHighSurrogate(ch)) {
            if (j == searchLast) {
              // missing low surrogate, fine, like String.indexOf(String)
              return true;
            }
            if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
              return true;
            }
          } else {
            // ch is in the Basic Multilingual Plane
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * <p>
   * Checks if the CharSequence contains any character in the given set of characters.
   * </p>
   *
   * <p>
   * A {@code null} CharSequence will return {@code false}. A {@code null} search CharSequence will
   * return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.containsAny(null, *)            = false
   * StringUtils.containsAny("", *)              = false
   * StringUtils.containsAny(*, null)            = false
   * StringUtils.containsAny(*, "")              = false
   * StringUtils.containsAny("zzabyycdxx", "za") = true
   * StringUtils.containsAny("zzabyycdxx", "by") = true
   * StringUtils.containsAny("aba","z")          = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @param searchChars the chars to search for, may be null
   * @return the {@code true} if any of the chars are found, {@code false} if no match or null input
   * @since 2.4
   * @since 3.0 Changed signature from containsAny(String, String) to containsAny(CharSequence,
   *        CharSequence)
   */
  public static boolean containsAny(final CharSequence cs, final CharSequence searchChars) {
    if (searchChars == null) {
      return false;
    }
    return containsAny(cs, toCharArray(searchChars));
  }

  /**
   * <p>
   * Checks if the CharSequence contains any of the CharSequences in the given array.
   * </p>
   *
   * <p>
   * A {@code null} CharSequence will return {@code false}. A {@code null} or zero length search
   * array will return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.containsAny(null, *)            = false
   * StringUtils.containsAny("", *)              = false
   * StringUtils.containsAny(*, null)            = false
   * StringUtils.containsAny(*, [])              = false
   * StringUtils.containsAny("abcd", "ab", "cd") = false
   * StringUtils.containsAny("abc", "d", "abc")  = true
   * </pre>
   *
   * 
   * @param cs The CharSequence to check, may be null
   * @param searchCharSequences The array of CharSequences to search for, may be null
   * @return {@code true} if any of the search CharSequences are found, {@code false} otherwise
   * @since 3.4
   */
  public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
    if (isEmpty(cs) || isEmpty(searchCharSequences)) {
      return false;
    }
    for (CharSequence searchCharSequence : searchCharSequences) {
      if (contains(cs, searchCharSequence)) {
        return true;
      }
    }
    return false;
  }

  // Contains
  // -----------------------------------------------------------------------
  /**
   * <p>
   * Checks if CharSequence contains a search character, handling {@code null}. This method uses
   * {@link String#indexOf(int)} if possible.
   * </p>
   *
   * <p>
   * A {@code null} or empty ("") CharSequence will return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.contains(null, *)    = false
   * StringUtils.contains("", *)      = false
   * StringUtils.contains("abc", 'a') = true
   * StringUtils.contains("abc", 'z') = false
   * </pre>
   *
   * @param seq the CharSequence to check, may be null
   * @param searchChar the character to find
   * @return true if the CharSequence contains the search character, false if not or {@code null}
   *         string input
   * @since 2.0
   * @since 3.0 Changed signature from contains(String, int) to contains(CharSequence, int)
   */
  public static boolean contains(final CharSequence seq, final int searchChar) {
    if (isEmpty(seq)) {
      return false;
    }
    return indexOf(seq, searchChar, 0) >= 0;
  }

  /**
   * <p>
   * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses
   * {@link String#indexOf(String)} if possible.
   * </p>
   *
   * <p>
   * A {@code null} CharSequence will return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.contains(null, *)     = false
   * StringUtils.contains(*, null)     = false
   * StringUtils.contains("", "")      = true
   * StringUtils.contains("abc", "")   = true
   * StringUtils.contains("abc", "a")  = true
   * StringUtils.contains("abc", "z")  = false
   * </pre>
   *
   * @param seq the CharSequence to check, may be null
   * @param searchSeq the CharSequence to find, may be null
   * @return true if the CharSequence contains the search CharSequence, false if not or {@code null}
   *         string input
   * @since 2.0
   * @since 3.0 Changed signature from contains(String, String) to contains(CharSequence,
   *        CharSequence)
   */
  public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
    if (seq == null || searchSeq == null) {
      return false;
    }
    return indexOf(seq, searchSeq, 0) >= 0;
  }

  /**
   * <p>
   * Checks if CharSequence contains a search CharSequence irrespective of case, handling
   * {@code null}. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}.
   *
   * <p>
   * A {@code null} CharSequence will return {@code false}.
   * </p>
   *
   * <pre>
   * StringUtils.contains(null, *) = false
   * StringUtils.contains(*, null) = false
   * StringUtils.contains("", "") = true
   * StringUtils.contains("abc", "") = true
   * StringUtils.contains("abc", "a") = true
   * StringUtils.contains("abc", "z") = false
   * StringUtils.contains("abc", "A") = true
   * StringUtils.contains("abc", "Z") = false
   * </pre>
   *
   * @param str the CharSequence to check, may be null
   * @param searchStr the CharSequence to find, may be null
   * @return true if the CharSequence contains the search CharSequence irrespective of case or false
   *         if not or {@code null} string input
   * @since 3.0 Changed signature from containsIgnoreCase(String, String) to
   *        containsIgnoreCase(CharSequence, CharSequence)
   */
  public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
    if (str == null || searchStr == null) {
      return false;
    }
    final int len = searchStr.length();
    final int max = str.length() - len;
    for (int i = 0; i <= max; i++) {
      if (regionMatches(str, true, i, searchStr, 0, len)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether the given CharSequence contains any whitespace characters.
   * 
   * @param seq the CharSequence to check (may be {@code null})
   * @return {@code true} if the CharSequence is not empty and contains at least 1 whitespace
   *         character
   * @see java.lang.Character#isWhitespace
   * @since 3.0
   */
  // From org.springframework.util.StringUtils, under Apache License 2.0
  public static boolean containsWhitespace(final CharSequence seq) {
    if (isEmpty(seq)) {
      return false;
    }
    final int strLen = seq.length();
    for (int i = 0; i < strLen; i++) {
      if (Character.isWhitespace(seq.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  // -----------------------------------------------------------------------
  /**
   * <p>
   * Finds the first index in the {@code CharSequence} that matches the specified character.
   * </p>
   *
   * @param cs the {@code CharSequence} to be processed, not null
   * @param searchChar the char to be searched for
   * @param start the start index, negative starts at the string start
   * @return the index where the search char was found, -1 if not found
   */
  public static int indexOf(final CharSequence cs, final int searchChar, int start) {
    if (cs instanceof String) {
      return ((String) cs).indexOf(searchChar, start);
    }
    final int sz = cs.length();
    if (start < 0) {
      start = 0;
    }
    for (int i = start; i < sz; i++) {
      if (cs.charAt(i) == searchChar) {
        return i;
      }
    }
    return NOT_FOUND;
  }

  /**
   * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
   *
   * @param cs the {@code CharSequence} to be processed
   * @param searchChar the {@code CharSequence} to be searched for
   * @param start the start index
   * @return the index where the search sequence was found
   */
  public static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
    return cs.toString().indexOf(searchChar.toString(), start);
  }

  /**
   * Green implementation of toCharArray.
   *
   * @param cs the {@code CharSequence} to be processed
   * @return the resulting char array
   */
  public static char[] toCharArray(final CharSequence cs) {
    if (cs instanceof String) {
      return ((String) cs).toCharArray();
    }
    final int sz = cs.length();
    final char[] array = new char[cs.length()];
    for (int i = 0; i < sz; i++) {
      array[i] = cs.charAt(i);
    }
    return array;
  }

  /**
   * Green implementation of regionMatches.
   *
   * @param cs the {@code CharSequence} to be processed
   * @param ignoreCase whether or not to be case insensitive
   * @param thisStart the index to start on the {@code cs} CharSequence
   * @param substring the {@code CharSequence} to be looked for
   * @param start the index to start on the {@code substring} CharSequence
   * @param length character length of the region
   * @return whether the region matched
   */
  public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
      final CharSequence substring, final int start, final int length) {
    if (cs instanceof String && substring instanceof String) {
      return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
    }
    int index1 = thisStart;
    int index2 = start;
    int tmpLen = length;

    while (tmpLen-- > 0) {
      final char c1 = cs.charAt(index1++);
      final char c2 = substring.charAt(index2++);

      if (c1 == c2) {
        continue;
      }

      if (!ignoreCase) {
        return false;
      }

      // The same check as in String.regionMatches():
      if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
          && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
        return false;
      }
    }

    return true;
  }

}
