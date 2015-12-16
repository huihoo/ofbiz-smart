package org.huihoo.ofbiz.smart.entity;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhcacheDemo {

    public static void main(String args[]) throws InterruptedException {

        CacheManager cacheManager = CacheManager.create();


        Cache testCache = new Cache(
                new CacheConfiguration("testCache", 1000)
                        .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                        .eternal(false)
                        .timeToLiveSeconds(60)
                        .timeToIdleSeconds(30)
                        .diskExpiryThreadIntervalSeconds(0)
                        .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP)));

        cacheManager.addCache(testCache);
        Element cacheEle = new Element("abc","abcdefg",60,6);
        testCache.put(cacheEle);

        String obj = (String) testCache.get("abc").getObjectValue();
        System.out.println(">>>" + obj);

        System.out.println(testCache.getStatistics().cacheHitCount());

        Thread.sleep(7000);

        obj = (String) testCache.get("abc").getObjectValue();
        System.out.println(">>>" + obj);
    }
}
