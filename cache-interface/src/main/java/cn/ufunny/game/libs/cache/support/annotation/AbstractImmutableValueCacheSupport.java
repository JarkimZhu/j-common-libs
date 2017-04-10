package cn.ufunny.game.libs.cache.support.annotation;


import cn.ufunny.game.libs.cache.iface.support.IValueCacheSupport;
import cn.ufunny.game.libs.cache.support.BaseImmutableCacheSupport;

import java.util.List;


public abstract class AbstractImmutableValueCacheSupport<K, V> extends AbstractCacheSupport<K, V>
		implements IValueCacheSupport<K, V>,BaseImmutableCacheSupport {


	protected abstract List<V> loadAllFromDatabase();


	@Override
	public void customInit(){
		customInit(findAll());
	}

	protected abstract void customInit(List<V> vs);

}
