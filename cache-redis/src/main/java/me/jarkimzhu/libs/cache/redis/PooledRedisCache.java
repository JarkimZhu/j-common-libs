package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created on 2017/4/11.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class PooledRedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements ICache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(PooledRedisCache.class);

    private JedisPool jedisPool;
    private RedisSupport<K, V> support;

    public PooledRedisCache(JedisPool jedisPool) {
        super(null);
        this.jedisPool = jedisPool;
        support = new RedisSupport<>(keyClass, valueClass);
    }

    public PooledRedisCache(String cacheName, JedisPool jedisPool) {
        super(cacheName);
        this.jedisPool = jedisPool;
        support = new RedisSupport<>(keyClass, valueClass);
    }

    public PooledRedisCache(String cacheName, JedisPool jedisPool, Type keyClass, Type valueClass) {
        super(cacheName, keyClass, valueClass);
        this.jedisPool = jedisPool;
        support = new RedisSupport<>(this.keyClass, this.valueClass);
    }

    public PooledRedisCache(JedisPool jedisPool, Type keyClass, Type valueClass) {
        super(null, keyClass, valueClass);
        this.jedisPool = jedisPool;
        support = new RedisSupport<>(this.keyClass, this.valueClass);
    }

    @Override
    public long size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.dbSize();
        }
    }

    @Override
    public boolean containsKey(K key) {
        try (
                Jedis jedis = jedisPool.getResource();
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
        logger.info("This method will cost large memory to compare.");
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
                Jedis jedis = jedisPool.getResource();
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
                Jedis jedis = jedisPool.getResource();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            s.put(key, value);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        try (
                Jedis jedis = jedisPool.getResource();
                RedisSupport<K, V> s = support.begin(jedis)
        ) {
            V old = s.get(key);
            if(old == null) {
                s.put(key, value);
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
                Jedis jedis = jedisPool.getResource();
                RedisSupport<K, V> s = support.begin(jedis);
        ) {
            s.del(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            if(ReflectionUtils.isPrimitiveOrWrapper(keyClass)) {
                return (Set<K>) jedis.keys("*");
            } else {
                Set<byte[]> keys = jedis.keys(SafeEncoder.encode("*"));
                Set<K> keySet = new HashSet<>(keys.size());
                for (byte[] bytes : keys) {
                    keySet.add(ObjectUtils.deserialize(bytes));
                }
                return keySet;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<byte[]> keySet = jedis.keys(SafeEncoder.encode("*"));
            int size = keySet.size();
            byte[][] keys = keySet.toArray(new byte[size][]);
            byte[][] values = jedis.mget(keys).toArray(new byte[size][]);

            Collection<V> result = new ArrayList<>(size);
            for(int i = 0; i < size; i++) {
                byte[] bValues = values[i];
                if(ReflectionUtils.isPrimitiveOrWrapper(valueClass)) {
                    result.add((V) CommonUtils.getValueByType(SafeEncoder.encode(bValues), valueClass));
                } else {
                    result.add(ObjectUtils.deserialize(bValues));
                }
            }
            return result;
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
                Jedis jedis = jedisPool.getResource();
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
