# 缓存

## Cache

缓存接口

```java
  void setEvictionStrategy(EvictionStrategy evictionStrategy);
  EvictionStrategy getEvictionStrategy();
  void start(String name);
  void shutDown();
  void setMaxEntries(int maxEntries);
  void setTimeToLiveSeconds(int timeToLiveSeconds);
  String getName();
  V put(K key, V value, int liveInSeconds);
  V put(K key, V value);
  V get(K key);
  V remove(K key);
  long getHitCount();
  long getMissCount();
  void clear();
  Map<K, V> getItems(int n);
```

## DefaultCache

实现了**Cache**接口，OFBiz Smart的缓存默认实现，基于[ehcache](http://www.ehcache.org/)。

## SimpleCacheManager

简单的缓存管理类，负责**Cache**的获取，创建，启动，退出，清理。

示例：

```
String cacheName = "EntityCache";
Cache<String, Object> ENTITY_CACHE = (Cache<String, Object>) 
                                         SimpleCacheManager.createCache(cacheName);
                                       
SimpleCacheManager.clear(cacheName);
SimpleCacheManager.shutDown(cacheName);

```

## 自定义缓存

1. 实现**Cache**接口

2. 在application.properties中配置

```
cache.provider=your.cache.class.name
```

## OFBiz Smart中的缓存

1. 名为**EntityCache**的缓存,用于实体引擎的实体缓存
2. 名为**I18N-resources-cache**的缓存，用于缓存国际化资源文件
3. 名为**Request-View-Cache**的缓存，用于缓存**View**的实例
4. 名为**Request-Handler-EntityClazz-Cache**的缓存，用于缓存加载的实体
