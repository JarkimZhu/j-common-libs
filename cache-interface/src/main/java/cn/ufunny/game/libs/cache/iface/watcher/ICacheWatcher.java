/**
 * ICacheWatcher.java
 */
package cn.ufunny.game.libs.cache.iface.watcher;

import java.util.Map;

/**
 * @author JarkimZhu
 *
 */
public interface ICacheWatcher {
	String[] findAllCacheName();
	void watch(String cacheName);
	<K, V> void watch(String cacheName, ICacheValueFilter<K, V> filter);
	<K, V> Map<K, V> findAll(String cacheName);
	<K, V> Map<K, V> findAll(String cacheName, ICacheValueFilter<K, V> filter);
}
