/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 2017/5/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public interface IFieldCache<K, F, V> extends ICache<K, V> {
    long size(K key);

    boolean isEmpty(K key);

    boolean containsField(K key, F field);

    V get(K key, F field);

    void put(K key, F field, V value);

    V putIfAbsent(K key, F field, V value);

    void remove(K key, F field);

    void clear(K key);

    Collection<V> values(K key);

    Map<F, V> queryFields(K key, Object param);
}
