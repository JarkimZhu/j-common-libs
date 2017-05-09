/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis.cluster;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.redis.RedisSupport;
import me.jarkimzhu.libs.cache.redis.utils.BulkReplyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisClusterException;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
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
        super(null, -1);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(null, keyClass, valueClass);
    }

    public ClusterRedisCache(JedisCluster jedisCluster, Class<K> keyClass, Class<V> valueClass) {
        super(null, -1, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(null, this.keyClass, this.valueClass);
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster) {
        super(cacheName, -1);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(cacheName, keyClass, valueClass);
    }

    public ClusterRedisCache(String cacheName, JedisCluster jedisCluster, Class<K> keyClass, Class<V> valueClass) {
        super(cacheName, -1, keyClass, valueClass);
        this.jedisCluster = jedisCluster;
        support = new RedisSupport<>(cacheName, this.keyClass, this.valueClass);
    }

    @Override
    public long size() {
        long size = 0;
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()){
            logger.debug("Getting size from: {}", k);
            JedisPool jp = clusterNodes.get(k);
            try (Jedis jedis = jp.getResource()) {
                Map<String, Map<String, String>> result = BulkReplyParser.parseInfo(jedis.info("Replication"));
                String role = result.get("Replication").get("role");
                if("slave".equalsIgnoreCase(role)) {
                    try (RedisSupport<K, V> s = support.begin(jedis)) {
                        size += s.dbSize();
                    }
                }
            } catch(Exception e){
                logger.error("Getting size error: {}", e);
            }
        }
        return size;
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
        try (RedisSupport<K, V> s = support.begin(jedisCluster)) {
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
        Set<K> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()){
            logger.debug("Getting keys from: {}", k);
            JedisPool jp = clusterNodes.get(k);
            try (Jedis jedis = jp.getResource()) {
                Map<String, Map<String, String>> result = BulkReplyParser.parseInfo(jedis.info("Replication"));
                String role = result.get("Replication").get("role");
                if("slave".equalsIgnoreCase(role)) {
                    try (RedisSupport<K, V> s = support.begin(jedis)) {
                        keys.addAll(s.keys("*"));
                    }
                }
            } catch(Exception e){
                logger.error("Getting keys error: {}", e);
            }
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        logger.warn("This method[values] will cost very large memory from Redis Cluster");
        Collection<V> result = new ArrayList<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()) {
            logger.debug("Getting keys from: {}", k);
            JedisPool jp = clusterNodes.get(k);
            try (Jedis jedis = jp.getResource()) {
                Map<String, Map<String, String>> info = BulkReplyParser.parseInfo(jedis.info("Replication"));
                String role = info.get("Replication").get("role");
                if("master".equalsIgnoreCase(role)) {
                    try (RedisSupport<K, V> s = support.begin(jedis)) {
                        result.addAll(s.values());
                    }
                }
            } catch(Exception e){
                logger.error("Getting keys error: {}", e);
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Collection<V> query(Object param) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
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
