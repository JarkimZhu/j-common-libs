package cn.ufunny.game.libs.cache.support.inhert;

import cn.ufunny.game.libs.cache.iface.support.IAlterableValuesCacheSupport;
import cn.ufunny.game.libs.cache.support.BaseAlterableCacheSupport;


/**
 * 
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractAlterableValuesCacheSupport<K,T, V> extends AbstractValuesCacheSupport<K,T,V>
		implements BaseAlterableCacheSupport<K,V>, IAlterableValuesCacheSupport<K, T, V> {
	

}
