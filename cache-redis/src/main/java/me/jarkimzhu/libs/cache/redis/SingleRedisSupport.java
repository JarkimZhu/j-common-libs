package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class SingleRedisSupport<K extends Serializable, V extends Serializable> {

    private Class keyClass;
    private Class valueClass;

    public SingleRedisSupport() {
        this.keyClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.valueClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @SuppressWarnings("unchecked")
    public V get(Jedis jedis, K key) throws IOException, ClassNotFoundException {
        if (key instanceof String) {
            if (valueClass.isPrimitive()) {
                String sValue = jedis.get((String) key);
                return (V) CommonUtils.getValueByType(sValue, valueClass);
            } else {
                byte[] bytes = jedis.get(SafeEncoder.encode((String) key));
                return ObjectUtils.deserialize(bytes);
            }
        } else {
            return ObjectUtils.deserialize(jedis.get(ObjectUtils.serialize(key)));
        }
    }

    public void put(Jedis jedis, K key, V value) throws IOException {
        if (key instanceof String) {
            if (valueClass.isPrimitive()) {
                jedis.set((String) key, CommonUtils.toString(value));
            } else {
                jedis.set(SafeEncoder.encode((String) key), ObjectUtils.serialize(value));
            }
        } else {
            jedis.set(ObjectUtils.serialize(key), ObjectUtils.serialize(value));
        }
    }

    public boolean exists(Jedis jedis, K key) throws IOException {
        if (key instanceof String) {
            return jedis.exists((String) key);
        } else {
            return jedis.exists(ObjectUtils.serialize(key));
        }
    }

    public void del(Jedis jedis, K key) throws IOException {
        if (key instanceof String) {
            jedis.del((String) key);
        } else {
            jedis.del(ObjectUtils.serialize(key));
        }
    }
}
