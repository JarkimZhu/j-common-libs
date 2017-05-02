/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.redis.RedisSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created on 2017/4/11.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class PoolRedisCache<K extends Serializable, V extends Serializable> extends AbstractPooledRedisCache<K, V> implements ICache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(PoolRedisCache.class);

    public PoolRedisCache(JedisPool jedisPool) {
        super(null, -1);
        init(getCacheName(), jedisPool, new RedisSupport<>(getCacheName(), keyClass, valueClass));
    }

    public PoolRedisCache(JedisPool jedisPool, Class<K> keyClass, Class<V> valueClass) {
        super(null, -1, keyClass, valueClass);
        init(getCacheName(), jedisPool, new RedisSupport<>(getCacheName(), keyClass, valueClass));
    }

    public PoolRedisCache(String cacheName, JedisPool jedisPool) {
        super(cacheName, -1);
        init(getCacheName(), jedisPool, new RedisSupport<>(getCacheName(), keyClass, valueClass));
    }

    public PoolRedisCache(String cacheName, JedisPool jedisPool, Class<K> keyClass, Class<V> valueClass) {
        super(cacheName, -1, keyClass, valueClass);
        init(getCacheName(), jedisPool, new RedisSupport<>(getCacheName(), keyClass, valueClass));
    }

    @Override
    public long size() {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            return s.dbSize();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public boolean containsKey(K key) {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            return s.exists(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        logger.warn("This method[containsValue] will cost large memory to compare.");
        Collection<V> values = values();
        for(V v : values) {
            if(value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            return s.get(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            int timeout = (int) getTimeout();
            if(timeout > -1) {
                s.setex(key, value, timeout);
            } else {
                s.set(key, value);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            V old = s.get(key);
            if(old == null) {
                s.setnx(key, value, (int) getTimeout());
            }
            return old;
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void remove(K key) {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            s.del(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = getJedis()) {
            jedis.flushDB();
        }
    }

    @Override
    public Set<K> keySet() {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            return s.keys("*");
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            return s.values();
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        Set<K> keys = keySet();
        try (
                Jedis jedis = getJedis();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            for (K key : keys) {
                V value = s.get(key);
                action.accept(key, value);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
