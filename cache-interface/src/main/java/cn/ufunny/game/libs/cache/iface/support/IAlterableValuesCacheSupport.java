package cn.ufunny.game.libs.cache.iface.support;


/**
 * alterable values cache support,the cache value is alterable
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public interface IAlterableValuesCacheSupport<K,T,V> extends IAlterableCacheSupport<K,V>, IValuesCacheSuppot<K, T, V> {

	/**
	 * remove value from cache
	 * @param key
	 * @param valueKey
	 * @return
	 */
	public boolean putByValueKey(K key,T valueKey,V value);

	/**
	 * remove value from cache
	 * @param key
	 * @param valueKey
	 * @return
	 */
	public boolean removeByValueKey(K key,T valueKey);


}
