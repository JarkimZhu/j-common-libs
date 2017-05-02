/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.redis.HashRedisSupport;
import me.jarkimzhu.libs.cache.redis.RedisSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolHashRedisCache<F extends Serializable, V extends Serializable> extends AbstractPooledRedisCache<F, V> implements ICache<F, V> {

    private static final Logger logger = LoggerFactory.getLogger(PoolHashRedisCache.class);

    private Class<F> fieldClass;
    private HashRedisSupport<String, F, V> hashSupport;

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(JedisPool jedisPool) {
        super(null, -1);
        this.hashSupport = new HashRedisSupport<>(getCacheName(), String.class, fieldClass, valueClass);
        init(getCacheName(), jedisPool, (RedisSupport<F, V>) hashSupport);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(JedisPool jedisPool, Class<F> fieldClass, Class<V> valueClass) {
        super(null, -1, fieldClass, valueClass);
        this.hashSupport = new HashRedisSupport<>(getCacheName(), String.class, fieldClass, valueClass);
        init(getCacheName(), jedisPool, (RedisSupport<F, V>) hashSupport);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(String cacheName, JedisPool jedisPool) {
        super(cacheName, -1);
        this.hashSupport = new HashRedisSupport<>(getCacheName(), String.class, fieldClass, valueClass);
        init(getCacheName(), jedisPool, (RedisSupport<F, V>) hashSupport);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(String cacheName, JedisPool jedisPool, Class<F> fieldClass, Class<V> valueClass) {
        super(cacheName, -1, fieldClass, valueClass);
        this.hashSupport = new HashRedisSupport<>(getCacheName(), String.class, fieldClass, valueClass);
        init(getCacheName(), jedisPool, (RedisSupport<F, V>) hashSupport);
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean containsKey(F key) {
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public V get(F key) {
        return null;
    }

    @Override
    public void put(F key, V value) {

    }

    @Override
    public V putIfAbsent(F key, V value) {
        return null;
    }

    @Override
    public void remove(F key) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<F> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super F, ? super V> action) {

    }
}
