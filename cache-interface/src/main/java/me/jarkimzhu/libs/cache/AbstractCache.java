package me.jarkimzhu.libs.cache;

import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;

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

    private final String cacheName;
    private long timeout;

    AbstractCache(String cacheName) {
        this.cacheName = cacheName;
    }

    @SuppressWarnings("unchecked")
    protected AbstractCache(String cacheName, long timeout) {
        this.cacheName = cacheName;
        this.timeout = timeout;

        Type[] types = ReflectionUtils.getGenericTypes(getClass());
        if(types.length >= 2) {
            this.keyClass = (Class<K>) types[0];
            this.valueClass = (Class<V>) types[1];
        } else {
            throw new IllegalArgumentException("Internal error: AbstractCache not extends ParameterizedType");
        }
    }

    @SuppressWarnings("unchecked")
    protected AbstractCache(String cacheName, long timeout, Class<K> keyClass, Class<V> valueClass) {
        this.cacheName = cacheName;
        this.timeout = timeout;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    public void destroy() {
        clear();
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }
}
