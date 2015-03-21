package org.huihoo.ofbiz.smart.base.cache;

/**
 * <p>
 * 通用的缓存接口
 * </p>
 * 
 * @author huangbohua
 * @since 1.0
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
  /**
   * <p>
   * 将对象加入缓存，如果不存在，新加；如果存在，更新
   * </p>
   * 
   * @param key 对象的键
   * @param value 对象的值
   */
  public void put(K key, V value);

  /**
   * <p>
   * 根据键获得缓存的对象
   * </p>
   * 
   * @param key 要获取缓存对象的键
   * @return 如果找到，返回缓存的对象；否则，返回<code>null</code>
   */
  public V get(K key);

  /**
   * <p>
   * 清空缓存
   * </p>
   */
  public void clear();

  /**
   * <p>
   * 根据键删除缓存的对象
   * </p>
   * 
   * @param key 要删除对象的键
   */
  public void remove(K key);

}
