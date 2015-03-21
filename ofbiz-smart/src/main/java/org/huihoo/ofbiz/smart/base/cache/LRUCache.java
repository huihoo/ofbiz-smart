package org.huihoo.ofbiz.smart.base.cache;

import java.util.concurrent.atomic.AtomicLong;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

/**
 * <p>
 * 实现LRU算法的本地缓存实现，使用 <code>com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap</code>来实现
 * </p>
 * 
 * @author huangbohua
 * @since 1.0
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> implements Cache<K, V> {
  /**
   * 默认的并发级别
   */
  public static final int DEFAULT_CONCURENCY_LEVEL = 32;
  /**
   * 本地缓存Map
   */
  private final ConcurrentLinkedHashMap<K, V> cacheMap;

  /**
   * 缓存请求计数器
   */
  private final AtomicLong requests = new AtomicLong(0);
  /**
   * 缓存命中计数器
   */
  private final AtomicLong hits = new AtomicLong(0);
  /**
   * 最后一次缓存请求计数器
   */
  private final AtomicLong lastRequests = new AtomicLong(0);
  /**
   * 最后一次缓存命中计数器
   */
  private final AtomicLong lastHits = new AtomicLong(0);

  public LRUCache(int capacity) {
    this(capacity, DEFAULT_CONCURENCY_LEVEL);
  }

  public LRUCache(int capacity, int concurency) {
    cacheMap =
            new ConcurrentLinkedHashMap.Builder<K, V>().weigher(Weighers.<V>singleton())
                    .initialCapacity(capacity).maximumWeightedCapacity(capacity)
                    .concurrencyLevel(concurency).build();
  }

  @Override
  public void put(K key, V value) {
    cacheMap.put(key, value);
  }

  @Override
  public V get(K key) {
    V v = cacheMap.get(key);
    requests.incrementAndGet();
    if (v != null) hits.incrementAndGet();
    return v;
  }

  @Override
  public void clear() {
    cacheMap.clear();
    requests.set(0);
    hits.set(0);
  }

  @Override
  public void remove(K key) {
    cacheMap.remove(key);
  }

  /**
   * 实时获得当前缓存的命中率
   * 
   * @return 缓存命中率
   */
  public double getRecentHitRate() {
    long r = requests.get();
    long h = hits.get();
    try {
      return (double) (h - lastHits.get()) / (r - lastRequests.get());
    } finally {
      lastRequests.set(r);
      lastHits.set(h);
    }
  }

  public long getHits() {
    return hits.get();
  }

  public long getRequests() {
    return requests.get();
  }

}
