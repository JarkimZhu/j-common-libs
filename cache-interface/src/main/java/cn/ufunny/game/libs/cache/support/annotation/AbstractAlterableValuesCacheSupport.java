package cn.ufunny.game.libs.cache.support.annotation;


import cn.ufunny.game.libs.cache.support.BaseAlterableCacheSupport;

import cn.ufunny.game.libs.cache.iface.support.IAlterableValuesCacheSupport;

/**
 * 
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractAlterableValuesCacheSupport<K, T,V> extends AbstractValuesCacheSupport<K,T, V>
		implements BaseAlterableCacheSupport<K,V>, IAlterableValuesCacheSupport<K, T, V> {


}
