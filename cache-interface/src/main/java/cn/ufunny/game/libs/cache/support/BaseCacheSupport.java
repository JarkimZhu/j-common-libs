package cn.ufunny.game.libs.cache.support;

import cn.ufunny.game.libs.cache.iface.support.ICacheSupport;

import java.lang.reflect.ParameterizedType;

/**
 * Created by ryan on 2015/12/18.
 */
public abstract class BaseCacheSupport <K, V> implements ICacheSupport<K, V> {

    protected Class<K> kClazz;

    protected Class<V> vClazz;


    @SuppressWarnings("unchecked")
    public BaseCacheSupport(){
        this.kClazz = (Class<K>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        this.vClazz = (Class<V>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
    }


    public String findValueClassSimpleName(){
        return this.vClazz.getSimpleName();
    }

    protected <E> boolean hasKeyType(E key){
        return key.getClass().getSimpleName().equals(this.kClazz.getSimpleName());
    }

    protected <E> boolean hasValueType(E value){
        return value.getClass().getSimpleName().equals(this.vClazz.getSimpleName());
    }


}
