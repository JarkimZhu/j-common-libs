package cn.ufunny.game.libs.cache.iface.watcher;

/**
 * @author JarkimZhu
 * @param <K>
 * @param <V>
 *
 */
public interface ICacheValuesFilter<K, T, V> extends ICacheElementFilter {
	public boolean accept(K key1, T key2, V value);
}
