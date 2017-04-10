package cn.ufunny.game.libs.cache.support.inhert;


import java.lang.reflect.ParameterizedType;

public abstract class AbstractValuesCacheSupport<K,T,V> extends AbstractCacheSupport<K, V> {


	protected Class<T> tClazz;

	@SuppressWarnings("unchecked")
	public AbstractValuesCacheSupport(){
		this.tClazz = (Class<T>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
		this.vClazz = (Class<V>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[2]);
	}


	protected <E> boolean hasValueKeyType(E key){
		return key.getClass().getSimpleName().equals(this.tClazz.getSimpleName());
	}

}
