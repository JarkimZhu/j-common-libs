package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.AbstractCache;
import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.redis.utils.BulkReplyParser;
import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
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
                    HashMap<Integer, Set<byte[]>> slotMap = new HashMap<>();
                    Set<byte[]> bKeys = jedis.keys(SafeEncoder.encode("*"));
                    for(byte[] bytes : bKeys) {
                        int slot = JedisClusterCRC16.getSlot(bytes);
                        Set<byte[]> slotKeys = slotMap.computeIfAbsent(slot, k1 -> new HashSet<>());
                        slotKeys.add(bytes);
                    }

                    for(Map.Entry<Integer, Set<byte[]>> entry : slotMap.entrySet()) {
                        int slot = entry.getKey();
                        Set<byte[]> keySet = entry.getValue();
                        int size = keySet.size();
                        byte[][] keys = keySet.toArray(new byte[size][]);
                        byte[][] values = jedis.mget(keys).toArray(new byte[size][]);
                        // TODO 不同数据类型get方式不同
                        for(int i = 0; i < size; i++) {
                            byte[] bValues = values[i];
                            if(ReflectionUtils.isPrimitiveOrWrapper(valueClass)) {
                                result.add((V) CommonUtils.getValueByType(SafeEncoder.encode(bValues), valueClass));
                            } else {
                                result.add(ObjectUtils.deserialize(bValues));
                            }
                        }
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
    public void forEach(BiConsumer<? super K, ? super V> action) {
        throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }
}
