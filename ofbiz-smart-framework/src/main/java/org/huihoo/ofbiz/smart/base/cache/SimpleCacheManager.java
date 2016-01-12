package org.huihoo.ofbiz.smart.base.cache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.Log;

/**
 * <p>
 * 缓存管理器
 * </p>
 * 
 * @author huangbaihua
 * @sice 1.0
 *
 */
public class SimpleCacheManager {
  private final static String TAG = SimpleCacheManager.class.getName();

  private volatile static String cacheProviderName = "";

  private volatile static int timeToLiveSeconds;

  private volatile static int maxEntries;

  private final static Map<String, Cache<?, ?>> CACHES_MAP = new ConcurrentHashMap<>();


  static {
    Properties prop = new Properties();
    try {
      prop.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
      cacheProviderName = prop.getProperty(C.CACHE_PROVIDER_NAME, "org.huihoo.ofbiz.smart.base.cache.DefaultCache");
      timeToLiveSeconds = Integer.parseInt(prop.getProperty(C.CACHE_DEFAULT_TIMETOLIVESECONDS, "60"));
      maxEntries = Integer.parseInt(prop.getProperty(C.CACHE_DEFAULT_MAXENTRIES, "1024"));
      Log.i("Using cache provider is : " + cacheProviderName, TAG);
    } catch (MalformedURLException e) {
      Log.e(e, "Unable to load application config properties.", TAG);
    } catch (IOException e) {
      Log.e(e, "Unable to load application config properties.", TAG);
    }
  }

  public static Cache<?, ?> createCache(String name) {
    Cache<?, ?> cache = CACHES_MAP.get(name);
    if (cache == null) {
      cache = new DefaultCache<>();
      cache.setTimeToLiveSeconds(timeToLiveSeconds);
      cache.setMaxEntries(maxEntries);
      cache.start(name);
      CACHES_MAP.put(name, cache);
    }
    return cache;
  }

  public static void shutDown(String name) {
    Cache<?, ?> cache = CACHES_MAP.get(name);
    if (cache != null) {
      cache.shutDown();
      CACHES_MAP.remove(name);
    }
  }

  public static void clear(String name) {
    Cache<?, ?> cache = CACHES_MAP.get(name);
    if (cache != null) {
      cache.clear();
    }
  }


  public static Cache<?, ?> getCache(String name) {
    Cache<?, ?> cache = CACHES_MAP.get(name);
    return cache;
  }

  public static List<Cache<?, ?>> getAllCache() {
    List<Cache<?, ?>> cacheList = new ArrayList<>();
    Iterator<Entry<String, Cache<?, ?>>> iter = CACHES_MAP.entrySet().iterator();
    while (iter.hasNext()) {
      cacheList.add(iter.next().getValue());
    }
    return cacheList;
  }
}
