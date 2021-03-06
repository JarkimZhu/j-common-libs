package me.jarkimzhu.libs.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/4/7.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public interface ICache<K, V> {
    String getCacheName();

    long size();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);

    V get(K key);

    void put(K key, V value);

    V putIfAbsent(K key, V value);

    boolean putIfNotExists(K key, V value);

    void remove(K key);

    void clear();

    Set<K> keySet();

    Collection<V> values();

    Map<K, V> query(Object param);

    void forEach(BiConsumer<? super K, ? super V> action);

    void setTimeout(long timeout);

    long getTimeout();

    void destroy();
}