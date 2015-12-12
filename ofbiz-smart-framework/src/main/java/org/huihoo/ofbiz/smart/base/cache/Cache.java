package org.huihoo.ofbiz.smart.base.cache;

import java.util.Map;

public interface Cache<K, V> {
  V put(K key, V value);


  V get(K key);


  V remove(K key);


  long getHitCount();


  long getMissCount();


  void clear();


  Map<K, V> getItems(int n);
}
