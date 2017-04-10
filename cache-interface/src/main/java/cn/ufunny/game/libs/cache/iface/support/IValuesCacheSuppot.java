package cn.ufunny.game.libs.cache.iface.support;

import java.util.Collection;
import java.util.List;


/**
 * values cache support
 * 
 * @author ryan
 *
 * @param <K>
 * @param <T>
 * @param <V>
 */
public interface IValuesCacheSuppot<K,T,V> extends ICacheSupport<K, V> {

	/** cache has value key
	 * 
	 * @param key
	 * @param valueKey
	 * @return
	 */
	public boolean hasValueKey(K key,T valueKey);
	
	/**
	 * find cache values
	 * @param key
	 * @return
	 */
	public List<V> find(K key);
	
	/**
	 * find value by key and valueKey
	 * @param key
	 * @param valueKey
	 * @return
	 */
	public V findByValueKey(K key,T valueKey);

	/**
	 * find values by key and valueKey
	 * @param key
	 * @param valueKeys
	 * @return
	 */
	public List<V> findByValueKeys(K key,Collection<T> valueKeys);
	
}
