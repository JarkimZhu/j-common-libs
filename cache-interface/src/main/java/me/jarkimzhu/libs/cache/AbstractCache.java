package me.jarkimzhu.libs.cache;

import java.lang.reflect.ParameterizedType;

/**
 * Created on 2017/4/12.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public abstract class AbstractCache<K, V> implements ICache<K, V> {

    protected Class keyClass;
    protected Class valueClass;

    private String cacheName;

    protected AbstractCache(String cacheName) {
        this.cacheName = cacheName;
        this.keyClass = (Class) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.valueClass = (Class) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];
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
