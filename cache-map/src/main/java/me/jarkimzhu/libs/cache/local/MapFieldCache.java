/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.local;

import me.jarkimzhu.libs.cache.AbstractFieldCache;
import me.jarkimzhu.libs.cache.IFieldCache;
import me.jarkimzhu.libs.cache.utils.CacheUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class MapFieldCache<K, F, V> extends AbstractFieldCache<K, F, V> implements IFieldCache<K, F, V> {

    private final F defaultField = null;

    private Map<K, Map<F, V>> store;

    public MapFieldCache() {
        this(null);
    }

    public MapFieldCache(String cacheName) {
        super(cacheName, -1);
        this.store = new ConcurrentHashMap<>();
    }

    public MapFieldCache(String cacheName, MapCacheBuilder builder) {
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

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean containsValue(V value) {
        return store.containsValue(value);
    }

    @Override
    public V get(K key) {
        return store.get(key).get(defaultField);
    }

    @Override
    public void put(K key, V value) {
        put(key, defaultField, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putIfAbsent(key, defaultField, value);
    }

    @Override
    public boolean putIfNotExists(K key, V value) {
        return putIfNotExists(key, defaultField, value);
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
        Collection<Map<F, V>> values = store.values();
        ArrayList<V> defaultValues = new ArrayList<>(values.size() / 2);
        for(Map<F, V> map : values) {
            if(map.containsKey(defaultField)) {
                defaultValues.add(map.get(defaultField));
            }
        }
        return defaultValues;
    }

    @Override
    public Map<K, V> query(Object param) {
        //TODO implement
        throw new NotImplementedException();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, Map<F, V>> entry : store.entrySet()) {
            K k;
            Map<F, V> map;
            try {
                k = entry.getKey();
                map = entry.getValue();
                if(map.containsKey(defaultField)) {
                    action.accept(k, map.get(defaultField));
                }
            } catch(IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    @Override
    public long size(K key) {
        return store.containsKey(key) ? store.get(key).size() : 0;
    }

    @Override
    public boolean containsField(K key, F field) {
        return store.containsKey(key) && store.get(key).containsKey(field);
    }

    @Override
    public V get(K key, F field) {
        return store.containsKey(key) ? store.get(key).get(field) : null;
    }

    @Override
    public void put(K key, F field, V value) {
        Map<F, V> map = store.computeIfAbsent(key, k -> new HashMap<>());
        map.put(field, value);
    }

    @Override
    public V putIfAbsent(K key, F field, V value) {
        Map<F, V> map = store.computeIfAbsent(key, k -> new HashMap<>());
        return map.putIfAbsent(field, value);
    }

    @Override
    public boolean putIfNotExists(K key, F field, V value) {
        return putIfAbsent(key, field, value) == null;
    }

    @Override
    public void remove(K key, F field) {
        if(store.containsKey(key)) {
            store.get(key).remove(field);
        }
    }

    @Override
    public void clear(K key) {
        if(store.containsKey(key)) {
            store.get(key).clear();
        }
    }

    @Override
    public Set<F> fieldSet(K key) {
        if(store.containsKey(key)) {
            return store.get(key).keySet();
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<V> values(K key) {
        if(store.containsKey(key)) {
            return store.get(key).values();
        }
        return Collections.emptyList();
    }

    @Override
    public Map<F, V> queryFields(K key, Object param) {
        if(store.containsKey(key)) {
            Map<F, V> all = store.get(key);
            return CacheUtils.filter(all, param);
        }
        return Collections.emptyMap();
    }
}
