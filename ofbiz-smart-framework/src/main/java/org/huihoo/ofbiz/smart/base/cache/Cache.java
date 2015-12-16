package org.huihoo.ofbiz.smart.base.cache;

import java.util.Map;

public interface Cache<K, V> {

  public final static int DEFAULT_TIME_TO_LIVE_SECONDS = 60;
  public final static int DEFAULT_TIME_TO_IDLE_SECONDS = 60;
  public final static int DEFAULT_MAX_ENTRIES = 1024;

  public final static EvictionStrategy LEAST_RECENTLY_USED = EvictionStrategy.LEAST_RECENTLY_USED;
  public final static EvictionStrategy LEAST_FREQUENTLY_USED = EvictionStrategy.LEAST_FREQUENTLY_USED;
  public final static EvictionStrategy LEAST_RECENTLY_ADDED = EvictionStrategy.LEAST_RECENTLY_ADDED;

  enum EvictionStrategy { LEAST_RECENTLY_USED, LEAST_FREQUENTLY_USED, LEAST_RECENTLY_ADDED }

  void setEvictionStrategy(EvictionStrategy evictionStrategy);

  EvictionStrategy getEvictionStrategy();

  void start(String name);

  void shutDown();

  void setMaxEntries(int maxEntries);

  void setTimeToLiveSeconds(int timeToLiveSeconds);

  void setTimeToIdleSeconds(int timeToIdleSeconds);

  String getName();

  V put(K key, V value,int liveInSeconds);


  V put(K key, V value);


  V get(K key);


  V remove(K key);


  long getHitCount();


  long getMissCount();


  void clear();


  Map<K, V> getItems(int n);
}
