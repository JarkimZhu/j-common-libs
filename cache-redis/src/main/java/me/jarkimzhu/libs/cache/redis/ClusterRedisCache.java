package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisClusterException;

import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(ClusterRedisCache.class);

    private JedisCluster jedisCluster;
    private RedisSupport<K, V> support;

    public ClusterRedisCache(JedisCluster jedisCluster) {
        super(null);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(keyClass, valueClass);
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster) {
        super(cacheName);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(keyClass, valueClass);
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster, Type keyClass, Type valueClass) {
        super(cacheName, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(this.keyClass, this.valueClass);
    }

    public ClusterRedisCache(JedisCluster jedisCluster, Type keyClass, Type valueClass) {
        super(null, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(this.keyClass, this.valueClass);
    }

    @Override
    public long size() {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    @Override
    public boolean containsKey(K key) {
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
            return s.exists(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    @Override
    public V get(K key) {
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
            return s.get(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
            s.put(key, value);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
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
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
            s.del(key);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    @Override
    public Set<K> keySet() {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    @Override
    public Collection<V> values() {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }
}
