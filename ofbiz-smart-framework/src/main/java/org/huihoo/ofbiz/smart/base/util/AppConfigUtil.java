package org.huihoo.ofbiz.smart.base.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;

public class AppConfigUtil {

  private final static String TAG = AppConfigUtil.class.getName();
  private final static Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>();
  private static String currentProfile;
  private static Properties currentProperties;
  
  static {
    Properties mainProp = new Properties();
    try {
      mainProp.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
      loadPropertiesInMap(mainProp, null);
      currentProperties = mainProp;
      
      currentProfile = mainProp.getProperty("profile");
      String activeProfiles = mainProp.getProperty("active.profiles");

      if (CommUtil.isNotEmpty(activeProfiles)) {
        String[] activeTokens = activeProfiles.split(",");
        for (String ap : activeTokens) {
          Properties prop = new Properties();
          prop.load(FlexibleLocation.resolveLocation("/application-" + ap + ".properties").openStream());
          loadPropertiesInMap(prop, ap);
          if (ap.equalsIgnoreCase(currentProfile)) {
            currentProperties = prop;
          }
        }
      }
    } catch (IOException e) {
      Log.e(e, "Load application config properties failed : " + e.getMessage(), TAG);
    }
  }

  public static Properties getCurentProperties() {
    return currentProperties;
  }
  
  public static String getProperty(String propertyName, String defaultValue) {
    String value = getPropertyByProfile(currentProfile, propertyName,defaultValue);
    if (CommUtil.isEmpty(value)) {
      value = defaultValue;
    }
    return value;
  }
  
  public static String getProperty(String propertyName) {
    return getPropertyByProfile(currentProfile, propertyName,null);
  }

  public static String getPropertyByProfile(String profile, String propertyName,String defaultValue) {
    String value = CONFIG_MAP.get(profile + "." + propertyName);
    if (CommUtil.isEmpty(value)) {
      value = CONFIG_MAP.get(currentProfile + "." + propertyName);
    }

    if (CommUtil.isEmpty(value)) {
      value = CONFIG_MAP.get(propertyName);
    }
    return value;
  }
  
  public static String getPropertyByProfile(String profile, String propertyName) {
    return getPropertyByProfile(profile, propertyName, null);
  }

 

  private static void loadPropertiesInMap(Properties prop, String profile) {
    if (prop != null) {
      Iterator<Entry<Object, Object>> entryIter = prop.entrySet().iterator();
      while (entryIter.hasNext()) {
        Entry<Object, Object> entry = entryIter.next();
        String key = (String) entry.getKey();
        String value = (String) entry.getValue();
        if (profile != null) {
          CONFIG_MAP.put(profile + "." + key, value);
        } else {
          CONFIG_MAP.put(key, value);
        }
      }
    }
  }

}
