package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class SingleRedisSupport<K extends Serializable, V extends Serializable> {

    private Class<K> keyClass;
    private Class<V> valueClass;

    public SingleRedisSupport(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    public V get(Jedis jedis, K key) throws IOException, ClassNotFoundException {
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            String sKey = CommonUtils.toString(key);
            if (ReflectionUtils.isPrimitiveOrWrapper(valueClass)) {
                String sValue = jedis.get(sKey);
                return (V) CommonUtils.getValueByType(sValue, valueClass);
            } else {
                byte[] bytes = jedis.get(SafeEncoder.encode(sKey));
                return ObjectUtils.deserialize(bytes);
            }
        } else {
            return ObjectUtils.deserialize(jedis.get(ObjectUtils.serialize(key)));
        }
    }

    public void put(Jedis jedis, K key, V value) throws IOException {
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            String sKey = CommonUtils.toString(key);
            if (ReflectionUtils.isPrimitiveOrWrapper(value.getClass())) {
                jedis.set(sKey, CommonUtils.toString(value));
            } else {
                jedis.set(SafeEncoder.encode(sKey), ObjectUtils.serialize(value));
            }
        } else {
            jedis.set(ObjectUtils.serialize(key), ObjectUtils.serialize(value));
        }
    }

    public boolean exists(Jedis jedis, K key) throws IOException {
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            return jedis.exists(CommonUtils.toString(key));
        } else {
            return jedis.exists(ObjectUtils.serialize(key));
        }
    }

    public void del(Jedis jedis, K key) throws IOException {
        if (ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            jedis.del(CommonUtils.toString(key));
        } else {
            jedis.del(ObjectUtils.serialize(key));
        }
    }
}
