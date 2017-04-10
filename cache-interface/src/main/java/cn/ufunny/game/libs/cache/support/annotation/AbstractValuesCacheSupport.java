package cn.ufunny.game.libs.cache.support.annotation;

import java.lang.reflect.ParameterizedType;

import cn.ufunny.game.libs.cache.exception.NoCacheableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ufunny.game.libs.cache.annotation.ValueField;

public abstract class AbstractValuesCacheSupport<K,T,V> extends AbstractCacheSupport<K, V> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractValuesCacheSupport.class);
	
	protected Class<T> tClazz;
	
	private ClassField defaultValueField;
	
	
	@SuppressWarnings("unchecked")
	public AbstractValuesCacheSupport(){
		this.tClazz = (Class<T>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
		this.vClazz = (Class<V>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[2]);
	}
	
	
	protected <E> boolean hasValueKeyType(E key){
		return key.getClass().getSimpleName().equals(this.tClazz.getSimpleName());
	}
	
	
	@SuppressWarnings("unchecked")
	protected T findValueKey(V value){
		if(this.defaultValueField == null){
			ClassField keyField = findClassFieldKey(this.vClazz, ValueField.class);
			this.defaultValueField = keyField;
			if(this.defaultValueField == null){
				logger.error("the cache value does not have a default cache value key");
				throw new NoCacheableException(this.vClazz);
			}
		}
		T valueKey;
		try {
			Object object = this.defaultValueField.findValue(value);
			if(object == null){
				logger.error("the cache value, value key is null ");
				throw new NoCacheableException(this.vClazz);
			}
			valueKey = (T)object;
		} catch (IllegalArgumentException e) {
			logger.error("the cache value does not have a cache value key, exception : {} ", e);
			throw new NoCacheableException(this.vClazz);
		}
		return valueKey;
	}
	
	
}
