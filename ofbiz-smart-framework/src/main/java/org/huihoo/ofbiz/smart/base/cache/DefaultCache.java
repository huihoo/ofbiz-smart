package org.huihoo.ofbiz.smart.base.cache;


import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.huihoo.ofbiz.smart.base.util.CommUtil;

import java.util.Map;

public class DefaultCache<K,V> implements Cache<K,V>{

    private final CacheManager CACHE_MANAGER = CacheManager.create();

    private volatile int timeToLiveSeconds;
    private volatile int maxEntries;

    private volatile EvictionStrategy evictionStrategy;
    private volatile String name;

    @Override
    public void setEvictionStrategy(EvictionStrategy evictionStrategy) {
        this.evictionStrategy = evictionStrategy;
    }

    @Override
    public EvictionStrategy getEvictionStrategy() {
        return evictionStrategy;
    }

    @Override
    public void start(String name) {
        if (CommUtil.isEmpty(name)) {
            name = "Cache-" + System.currentTimeMillis();
        }
        this.name = name;
        MemoryStoreEvictionPolicy mep = null;

        if (evictionStrategy == null) {
            mep = MemoryStoreEvictionPolicy.LRU;
        } else {
            if (evictionStrategy.equals(EvictionStrategy.LEAST_FREQUENTLY_USED)) {
                mep = MemoryStoreEvictionPolicy.LFU;
            } else if (evictionStrategy.equals(EvictionStrategy.LEAST_RECENTLY_USED)) {
                mep = MemoryStoreEvictionPolicy.LRU;
            } else if (evictionStrategy.equals(EvictionStrategy.LEAST_RECENTLY_ADDED)) {
                mep = MemoryStoreEvictionPolicy.FIFO;
            }
        }


        net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(
                new CacheConfiguration(name, maxEntries == 0 ? DEFAULT_MAX_ENTRIES : maxEntries)
            .memoryStoreEvictionPolicy(mep)
            .eternal(false)
            .timeToLiveSeconds(timeToLiveSeconds == 0 ? DEFAULT_TIME_TO_LIVE_SECONDS : timeToLiveSeconds)
            .diskExpiryThreadIntervalSeconds(0)
            .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP)));

        CACHE_MANAGER.addCache(cache);
    }

    @Override
    public void shutDown() {
        CACHE_MANAGER.shutdown();
    }

    @Override
    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    @Override
    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }


    @Override
    public String getName() {
        return name;
    }

    //===============================================================
    //                  Cache Operation
    //===============================================================
    @Override
    public V put(K key, V value, int liveInSeconds) {
        Element element = new Element(key,value,0, liveInSeconds);
        CACHE_MANAGER.getCache(name).putIfAbsent(element);
        return value;
    }

    @Override
    public V put(K key, V value) {
        return put(key,value,0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) {
        Element ele = CACHE_MANAGER.getCache(name).get(key);
        return ele == null ? null : (V) ele.getObjectValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(K key) {
        return (V) CACHE_MANAGER.getCache(name).removeAndReturnElement(key).getObjectValue();
    }

    @Override
    public long getHitCount() {
        return CACHE_MANAGER.getCache(name).getStatistics().cacheHitCount();
    }

    @Override
    public long getMissCount() {
        return CACHE_MANAGER.getCache(name).getStatistics().cacheMissCount();
    }

    @Override
    public void clear() {
        CACHE_MANAGER.clearAll();
    }

    @Override
    public Map<K, V> getItems(int n) {
        return null;
    }
}
