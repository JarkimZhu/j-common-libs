/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.AbstractFieldCache;
import me.jarkimzhu.libs.cache.IFieldCache;
import me.jarkimzhu.libs.cache.redis.HashRedisSupport;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolFieldRedisCache<K extends Serializable, F extends Serializable, V extends Serializable> extends AbstractFieldCache<K, F, V> implements IFieldCache<K, F, V> {

    private HashRedisSupport<K, F, V> hashRedisSupport;

    public PoolFieldRedisCache(String cacheName, long timeout) {
        super(cacheName, timeout);
    }

    public PoolFieldRedisCache(String cacheName, long timeout, Class<K> keyClass, Class<F> fieldClass, Class<V> valueClass) {
        super(cacheName, timeout, keyClass, fieldClass, valueClass);
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Map<K, V> query(Object param) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    @Override
    public long size(K key) {
        return 0;
    }

    @Override
    public boolean containsField(K key, F field) {
        return false;
    }

    @Override
    public V get(K key, F field) {
        return null;
    }

    @Override
    public void put(K key, F field, V value) {

    }

    @Override
    public V putIfAbsent(K key, F field, V value) {
        return null;
    }

    @Override
    public void remove(K key, F field) {

    }

    @Override
    public void clear(K key) {

    }

    @Override
    public Collection<V> values(K key) {
        return null;
    }

    @Override
    public Map<F, V> queryFields(K key, Object param) {
        return null;
    }
}
