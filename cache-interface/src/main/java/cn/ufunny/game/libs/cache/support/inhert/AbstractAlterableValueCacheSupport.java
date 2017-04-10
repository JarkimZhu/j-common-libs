package cn.ufunny.game.libs.cache.support.inhert;

import cn.ufunny.game.libs.cache.iface.support.IAlterableValueCacheSupport;
import cn.ufunny.game.libs.cache.support.BaseAlterableCacheSupport;


/**
 * alterable value cache support,the cache value is alterable
 *
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractAlterableValueCacheSupport<K, V> extends AbstractCacheSupport<K, V>
		implements BaseAlterableCacheSupport<K,V>, IAlterableValueCacheSupport<K, V> {
	

}
