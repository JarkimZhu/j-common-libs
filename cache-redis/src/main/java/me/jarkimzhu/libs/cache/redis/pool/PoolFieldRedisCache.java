/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.pool;

import me.jarkimzhu.libs.cache.AbstractFieldCache;
import me.jarkimzhu.libs.cache.IFieldCache;
import me.jarkimzhu.libs.cache.redis.HashRedisSupport;
import me.jarkimzhu.libs.cache.redis.utils.JedisUtils;
import me.jarkimzhu.libs.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolFieldRedisCache<K extends Serializable, F extends Serializable, V extends Serializable> extends AbstractFieldCache<K, F, V> implements IFieldCache<K, F, V> {

    private static final Logger logger = LoggerFactory.getLogger(PoolFieldRedisCache.class);

    private final F defaultField = null;

    private Pool<Jedis> jedisPool;
    private HashRedisSupport<K, F, V> support;
    private PoolRedisCache<K, V> delegate;
    private int database;

    @SuppressWarnings("unchecked")
    public PoolFieldRedisCache(JedisPool jedisPool) {
        super(null, -1);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolFieldRedisCache(JedisPool jedisPool, Class<K> keyClass, Class<F> fieldClass, Class<V> valueClass) {
        super(null, -1, keyClass, fieldClass, valueClass);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolFieldRedisCache(String cacheName, JedisPool jedisPool) {
        super(cacheName, -1);
        init(jedisPool);
    }

    @SuppressWarnings("unchecked")
    public PoolFieldRedisCache(String cacheName, JedisPool jedisPool, Class<K> keyClass, Class<F> fieldClass, Class<V> valueClass) {
        super(cacheName, -1, keyClass, fieldClass, valueClass);
        init(jedisPool);
    }

    private void init(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.support = new HashRedisSupport<>(null, keyClass, fieldClass, valueClass);
        this.delegate = new PoolRedisCache<>(jedisPool, keyClass, valueClass);
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public boolean containsKey(K key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        throw new NotImplementedException();
    }

    @Override
    public V get(K key) {
        return get(key, defaultField);
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
        delegate.remove(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return query("*").values();
    }

    @Override
    public Map<K, V> query(Object param) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            if(param instanceof Serializable) {
                Set<K> keySet = s.keys((Serializable) param);
                HashMap<K, V> values = new HashMap<>(keySet.size());
                for (K key : keySet) {
                    try {
                        values.put(key, s.hget(key, defaultField));
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
                return values;
            } else {
                throw new IllegalArgumentException("Only support Serializable param to Redis.");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        Set<K> keys = keySet();
        try (
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            for (K key : keys) {
                V value = s.hget(key, defaultField);
                action.accept(key, value);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public long size(K key) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hlen(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public boolean containsField(K key, F field) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hexists(key, field);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public V get(K key, F field) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hget(key, field);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void put(K key, F field, V value) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            s.hset(key, field, value);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public V putIfAbsent(K key, F field, V value) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            V old = s.hget(key, field);
            if(old == null) {
                if(s.hsetnx(key, field, value) != 0) {
                    return null;
                } else {
                    old = s.hget(key, field);
                }
            }
            return old;
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean putIfNotExists(K key, F field, V value) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hsetnx(key, field, value) != 0;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void remove(K key, F field) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            //noinspection unchecked
            s.hdel(key, field);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void clear(K key) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            s.clear(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public Set<F> fieldSet(K key) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hkeys(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<V> values(K key) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            return s.hvals(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public Map<F, V> queryFields(K key, Object param) {
        try(
                Jedis jedis = JedisUtils.getJedis(jedisPool, database);
                HashRedisSupport<K, F, V> s = support.begin(jedis)
        ) {
            Map<F, V> all = s.hgetall(key);
            return CacheUtils.filter(all, param);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }
}
