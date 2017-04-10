package cn.ufunny.game.libs.cache.iface.support;

import java.util.Collection;
import java.util.List;

/**
 * K,default cache key
 * V,cache value
 * 
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheSupport<K, V> {
	
	/**
	 * cache has key
	 * @param key
	 * @return
	 */
	public boolean hasKey(K key);

	/**
	 * cache has field key
	 * @param key
	 * @return
	 */
	public boolean hasFieldKey(String key);

	/**
	 * find all values by default keys
	 * @return
	 */
	public List<V> findAll();
	
	/**
	 * find values by keys, key is  default cache key
	 * @param keys
	 * @return
	 */
	public List<V> findByKeys(Collection<K> keys);
	
	/**
	 * find cache name
	 * @return
	 */
	public String findCacheName();

	/**
	 * total cache size
	 * @return
	 */
	public int size();
	
}
