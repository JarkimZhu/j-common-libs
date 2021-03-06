/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class JedisUtils {
    public static Jedis getJedis(Pool<Jedis> jedisPool, int database) {
        Jedis jedis = jedisPool.getResource();
        if(jedis.getDB() != database) {
            jedis.select(database);
        }
        return jedis;
    }
}
