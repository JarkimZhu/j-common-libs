package me.jarkimzhu.libs.cache;

import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created on 2017/4/12.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public abstract class AbstractCache<K, V> implements ICache<K, V> {

    protected Class<K> keyClass;
    protected Class<V> valueClass;

    private String cacheName;

    @SuppressWarnings("unchecked")
    protected AbstractCache(String cacheName) {
        this.cacheName = cacheName;
        Type superClass = this.getClass().getGenericSuperclass();
        if(superClass instanceof ParameterizedType) {
            Type keyType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            Type valueType = ((ParameterizedType) superClass).getActualTypeArguments()[1];
            if(keyType instanceof TypeVariableImpl || valueType instanceof TypeVariableImpl) {
                throw new IllegalArgumentException("Internal error: AbstractCache constructed without actual type information");
            } else {
                this.keyClass = (Class<K>) keyType;
                this.valueClass = (Class<V>) valueType;
            }
        } else {
            throw new IllegalArgumentException("Internal error: AbstractCache not extends ParameterizedType");
        }
    }

    @SuppressWarnings("unchecked")
    protected AbstractCache(String cacheName, Type keyClass, Type valueClass) {
        this.cacheName = cacheName;
        this.keyClass = (Class<K>) keyClass;
        this.valueClass = (Class<V>) valueClass;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }
}
