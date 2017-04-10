package cn.ufunny.game.libs.cache.iface.watcher;

/**
 * @author JarkimZhu
 * @param <K>
 * @param <V>
 *
 */
public interface ICacheValueFilter<K, V> extends ICacheElementFilter {
	public boolean accept(K key, V value);
}
