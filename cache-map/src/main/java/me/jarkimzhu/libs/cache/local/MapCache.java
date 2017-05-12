/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.local;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class MapCache<K, V> extends AbstractCache<K, V> implements ICache<K, V> {

    private Map<K, V> store;

    public MapCache() {
        this(null);
    }

    public MapCache(String cacheName) {
        super(cacheName, -1);
        this.store = new ConcurrentHashMap<>();
    }

    public MapCache(String cacheName, MapCacheBuilder builder) {
        super(cacheName, -1);
        //noinspection unchecked
        this.store = builder.buildStore();
    }

    @Override
    public long size() {
        return store.size();
    }

    @Override
    public boolean containsKey(K key) {
        return store.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return store.containsValue(value);
    }

    @Override
    public V get(K key) {
        return store.get(key);
    }

    @Override
    public void put(K key, V value) {
        store.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return store.putIfAbsent(key, value);
    }

    @Override
    public void remove(K key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public Set<K> keySet() {
        return store.keySet();
    }

    @Override
    public Collection<V> values() {
        return store.values();
    }

    @Override
    public Map<K, V> query(Object param) {
        //TODO implement
        throw new NotImplementedException();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        store.forEach(action);
    }
}
