/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis;

import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class HashRedisSupport<K extends Serializable, F extends Serializable, V extends Serializable> extends RedisSupport<K, V> {

    private Class<F> fieldClass;

    public HashRedisSupport(String namespace, Class<K> keyClass, Class<F> filedClass, Class<V> valueClass) {
        super(namespace, keyClass, valueClass);
        this.fieldClass = filedClass;
    }

    @Override
    public HashRedisSupport<K, F, V> begin(JedisCommands jedisCommands) {
        local.set(jedisCommands);
        return this;
    }

    public long hlen(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            return ((BinaryJedisClusterCommands) jedisCommands).hlen(redisKey);
        } else {
            return ((BinaryJedisCommands) jedisCommands).hlen(redisKey);
        }
    }
}
