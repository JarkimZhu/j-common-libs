package cn.ufunny.game.libs.cache.iface.support;



/**
 * 
 * value cache support
 * 
 * K,default cache key
 * V,cache value
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public interface IValueCacheSupport<K,V> extends ICacheSupport<K, V> {

	/**
	 * find value by default key
	 * @param key
	 * @return
	 */
	public V find(K key);
	
}
