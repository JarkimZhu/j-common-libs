package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/4/14.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class ClusterRedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements ICache<K, V> {

    private JedisCluster jedisCluster;

    public ClusterRedisCache(JedisCluster jedisCluster) {
        super(null);
        this.jedisCluster = jedisCluster;
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster) {
        super(cacheName);
        this.jedisCluster = jedisCluster;
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster, Type keyClass, Type valueClass) {
        super(cacheName, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
    }

    public ClusterRedisCache(JedisCluster jedisCluster, Type keyClass, Type valueClass) {
        super(null, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
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
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }
}
