package cn.ufunny.game.libs.cache.support.inhert;

import cn.ufunny.game.libs.cache.iface.support.IValuesCacheSuppot;
import cn.ufunny.game.libs.cache.inherit.ValueIdentifiable;
import cn.ufunny.game.libs.cache.support.BaseImmutableCacheSupport;

import java.util.List;


public abstract class AbstractImmutableValuesCacheSupport<K, T, V extends ValueIdentifiable<K,T>>
		extends AbstractValuesCacheSupport<K,T,V> implements IValuesCacheSuppot<K, T, V>,BaseImmutableCacheSupport {

	protected abstract List<V> loadAllFromDatabase();

	@Override
	public void customInit(){
		customInit(findAll());
	}

	protected abstract void customInit(List<V> vs);

}
