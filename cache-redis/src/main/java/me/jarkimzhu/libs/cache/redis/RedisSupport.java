package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class RedisSupport<K extends Serializable, V extends Serializable> implements Closeable {

    private Class<K> keyClass;
    private Class<V> valueClass;

    private ThreadLocal<JedisCommands> local = new ThreadLocal<>();

    public RedisSupport(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            String sKey = CommonUtils.toString(key);
            if (ReflectionUtils.isPrimitiveOrWrapper(valueClass)) {
                String sValue = jedisCommands.get(sKey);
                return (V) CommonUtils.getValueByType(sValue, valueClass);
            } else {
                byte[] bKey = SafeEncoder.encode(sKey);
                byte[] bValue;
                if (jedisCommands instanceof BinaryJedisClusterCommands) {
                    bValue = ((BinaryJedisClusterCommands) jedisCommands).get(bKey);
                } else {
                    bValue = ((BinaryJedisCommands) jedisCommands).get(bKey);
                }
                return ObjectUtils.deserialize(bValue);
            }
        } else {
            byte[] bKey = ObjectUtils.serialize(key);
            byte[] bValue;
            if (jedisCommands instanceof BinaryJedisClusterCommands) {
                bValue = ((BinaryJedisClusterCommands) jedisCommands).get(bKey);
            } else {
                bValue = ((BinaryJedisCommands) jedisCommands).get(bKey);
            }
            return ObjectUtils.deserialize(bValue);
        }
    }

    public void put(K key, V value) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            String sKey = CommonUtils.toString(key);
            if (ReflectionUtils.isPrimitiveOrWrapper(value.getClass())) {
                jedisCommands.set(sKey, CommonUtils.toString(value));
            } else {
                byte[] bKey = SafeEncoder.encode(sKey);
                byte[] bValue = ObjectUtils.serialize(value);
                if (jedisCommands instanceof BinaryJedisClusterCommands) {
                    ((BinaryJedisClusterCommands) jedisCommands).set(bKey, bValue);
                } else {
                    ((BinaryJedisCommands) jedisCommands).set(bKey, bValue);
                }
            }
        } else {
            byte[] bKey = ObjectUtils.serialize(key);
            byte[] bValue = ObjectUtils.serialize(value);
            if (jedisCommands instanceof BinaryJedisClusterCommands) {
                ((BinaryJedisClusterCommands) jedisCommands).set(bKey, bValue);
            } else {
                ((BinaryJedisCommands) jedisCommands).set(bKey, bValue);
            }
        }
    }

    public boolean exists(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            return jedisCommands.exists(CommonUtils.toString(key));
        } else {
            byte[] bKey = ObjectUtils.serialize(key);
            if (jedisCommands instanceof BinaryJedisClusterCommands) {
                return ((BinaryJedisClusterCommands) jedisCommands).exists(bKey);
            } else {
                return ((BinaryJedisCommands) jedisCommands).exists(bKey);
            }
        }
    }

    public void del(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            jedisCommands.del(CommonUtils.toString(key));
        } else {
            byte[] bKey = ObjectUtils.serialize(key);
            if (jedisCommands instanceof BinaryJedisClusterCommands) {
                ((BinaryJedisClusterCommands) jedisCommands).del(bKey);
            } else {
                ((BinaryJedisCommands) jedisCommands).del(bKey);
            }
        }
    }

    private JedisCommands getJedisCommands() {
        JedisCommands jedisCommands = local.get();
        if (jedisCommands == null) {
            throw new RuntimeException("Please call begin at first");
        }
        return jedisCommands;
    }

    public RedisSupport<K, V> begin(JedisCommands jedisCommands) {
        local.set(jedisCommands);
        return this;
    }

    public void commit() {
        local.set(null);
    }

    public void rollback() {
        local.set(null);
    }

    @Override
    public void close() throws IOException {
        local.set(null);
    }
}
