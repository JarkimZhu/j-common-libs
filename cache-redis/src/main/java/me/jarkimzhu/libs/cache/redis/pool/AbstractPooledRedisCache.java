/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.redis.RedisSupport;
import me.jarkimzhu.libs.utils.CommonUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class AbstractPooledRedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private static final Set<String> DB_INDEX = new HashSet<>(16);

    private JedisPool jedisPool;
    private int database;
    RedisSupport<K, V> support;

    AbstractPooledRedisCache(String cacheName, long timeout) {
        super(cacheName, timeout);
    }

    AbstractPooledRedisCache(String cacheName, long timeout, Class<K> keyClass, Class<V> valueClass) {
        super(cacheName, timeout, keyClass, valueClass);
    }

    void init(String cacheName, JedisPool jedisPool, RedisSupport<K, V> support) {
        this.jedisPool = jedisPool;
        if(!CommonUtils.isBlank(cacheName)) {
            DB_INDEX.add(cacheName);
            this.database = DB_INDEX.size() - 1;
        }
        this.support = support;
    }

    Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        if(jedis.getDB() != database) {
            jedis.select(database);
        }
        return jedis;
    }

    @Override
    public void destroy() {
        super.destroy();
        DB_INDEX.remove(this.getCacheName());
    }

    @Override
    public long getTimeout() {
        long timeout = super.getTimeout();
        if(timeout > -1) {
            return timeout / 1000;
        } else {
            return timeout;
        }
    }
}
