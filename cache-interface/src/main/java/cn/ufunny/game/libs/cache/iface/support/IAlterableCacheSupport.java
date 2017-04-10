package cn.ufunny.game.libs.cache.iface.support;

import java.util.Collection;

/**
 * @author JarkimZhu
 * @param <V>
 *
 */
public interface IAlterableCacheSupport<K,V> extends ICacheSupport<K,V> {

	/**
	 * add value to cache
	 * @param value
	 * @return
	 */
	public boolean put(V value);


	/**
	 * add value to cache
	 * @param value
	 * @return
	 */
	public boolean putIfAbsent(V value);

	/**
	 * add value to cache by key
	 * @param key
	 * @param value
	 * @return
	 */
	public <E> boolean putByKey(E key,V value);

	/**
	 * add value to cache by key
	 * @param key
	 * @param value
	 * @return
	 */
	public <E> boolean putIfAbsentByKey(E key, V value);

	/**
	 * add values to cache
	 * @param value
	 * @return
	 */
	public boolean putAll(Collection<V> value);

	/**
	 * add values to cache
	 * @param value
	 * @return
	 */
	public boolean putAllIfAbsent(Collection<V> value);

	/**
	 * remove value form cache
	 * if has the same key,remove the value from cache
	 * @param value
	 * @return
	 */
	public boolean remove(V value);

	/**
	 * remove value form cache by key
	 * @param key
	 * @return
	 */
	public boolean removeByKey(K key);

	/**
	 * remove values form cache by keys
	 * @param keys
	 */
	public boolean removeByKeys(Collection<K> keys);

	/**
	 * remove values from cache
	 * if has the same key,remove the value from cache
	 * @param values
	 */
	public boolean removeAll(Collection<V> values);

	/**
	 * clean cache
	 */
	public boolean clean();

	/**
	 * find all dirty data include change data
	 * @return
	 */
	public Collection<V> findDirtyOrChangeData();

	public <E> boolean hasKeyValueChange(E key);

}
