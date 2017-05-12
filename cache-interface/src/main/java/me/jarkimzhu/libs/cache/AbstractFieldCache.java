/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache;

import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;

import java.lang.reflect.Type;

/**
 * Created on 2017/5/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class AbstractFieldCache<K, F, V> extends AbstractCache<K, V> implements IFieldCache<K, F, V> {

    protected Class<F> fieldClass;

    @SuppressWarnings("unchecked")
    protected AbstractFieldCache(String cacheName, long timeout) {
        super(cacheName);
        setTimeout(timeout);

        Type[] types = ReflectionUtils.getGenericTypes(getClass());
        if(types.length >= 3) {
            this.keyClass = (Class<K>) types[0];
            this.fieldClass = (Class<F>) types[1];
            this.valueClass = (Class<V>) types[2];
        } else {
            throw new IllegalArgumentException("Internal error: AbstractCache not extends ParameterizedType");
        }
    }

    protected AbstractFieldCache(String cacheName, long timeout, Class<K> keyClass, Class<F> fieldClass, Class<V> valueClass) {
        super(cacheName, timeout, keyClass, valueClass);
        this.fieldClass = fieldClass;
    }

    @Override
    public boolean isEmpty(K key) {
        return size(key) <= 0;
    }
}
