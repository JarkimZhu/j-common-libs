/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.redis.HashRedisSupport;
import me.jarkimzhu.libs.cache.redis.utils.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolHashRedisCache<F extends Serializable, V extends Serializable> extends AbstractCache<F, V> implements ICache<F, V> {

    private static final Logger logger = LoggerFactory.getLogger(PoolHashRedisCache.class);

    private Class<F> fieldClass;
    private Pool<Jedis> jedisPool;
    private int database;
    private HashRedisSupport<String, F, V> support;

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(Pool<Jedis> jedisPool) {
        super(null, -1);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(Pool<Jedis> jedisPool, Class<F> fieldClass, Class<V> valueClass) {
        super(null, -1, fieldClass, valueClass);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(String cacheName, Pool<Jedis> jedisPool) {
        super(cacheName, -1);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolHashRedisCache(String cacheName, Pool<Jedis> jedisPool, Class<F> fieldClass, Class<V> valueClass) {
        super(cacheName, -1, fieldClass, valueClass);
        init(jedisPool);
    }

    private void init(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
        this.support = new HashRedisSupport<>(null, String.class, fieldClass, valueClass);
    }

    @Override
    public long size() {
        try (
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<String, F, V> s = support.begin(jedis)
        ) {
            return s.hlen(getCacheName());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return -1;
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
    public Map<F, V> query(Object param) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super F, ? super V> action) {

    }

    @Override
    public long getTimeout() {
        return JedisUtils.getTimeout(super.getTimeout());
    }
}
