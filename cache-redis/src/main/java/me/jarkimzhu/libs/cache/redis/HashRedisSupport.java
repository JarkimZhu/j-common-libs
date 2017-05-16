/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class HashRedisSupport<K extends Serializable, F extends Serializable, V extends Serializable> extends RedisSupport<K, V> {

    private Class<F> fieldClass;

    public HashRedisSupport(String namespace, Class<K> keyClass, Class<F> filedClass, Class<V> valueClass) {
        super(namespace, keyClass, valueClass);
        this.fieldClass = filedClass;
    }

    @Override
    public HashRedisSupport<K, F, V> begin(JedisCommands jedisCommands) {
        local.set(jedisCommands);
        return this;
    }

    public long hlen(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            return ((BinaryJedisClusterCommands) jedisCommands).hlen(redisKey);
        } else {
            return ((BinaryJedisCommands) jedisCommands).hlen(redisKey);
        }
    }

    public V hget(K key, F field) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] fieldKey = toRedisFieldKey(field);

        byte[] bValue;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            bValue = ((BinaryJedisClusterCommands) jedisCommands).hget(redisKey, fieldKey);
        } else {
            bValue = ((BinaryJedisCommands) jedisCommands).hget(redisKey, fieldKey);
        }
        return fromRedisValue(bValue);
    }

    public void hset(K key, F field, V value) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisFieldKey = toRedisFieldKey(field);
        byte[] redisValue = toRedisValue(value);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).hset(redisKey, redisFieldKey, redisValue);
        } else {
            ((BinaryJedisCommands) jedisCommands).hset(redisKey, redisFieldKey, redisValue);
        }
    }

    public int hsetnx(K key, F field, V value) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisFieldKey = toRedisFieldKey(field);
        byte[] redisValue = toRedisValue(value);

        long result;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            result = ((BinaryJedisClusterCommands) jedisCommands).hsetnx(redisKey, redisFieldKey, redisValue);
        } else {
            result = ((BinaryJedisCommands) jedisCommands).hsetnx(redisKey, redisFieldKey, redisValue);
        }
        return (int) result;
    }

    public Map<F, V> hgetall(K key) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        Map<byte[], byte[]> all;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            all = ((BinaryJedisClusterCommands) jedisCommands).hgetAll(redisKey);
        } else {
            all = ((BinaryJedisCommands) jedisCommands).hgetAll(redisKey);
        }

        Map<F, V> result = new HashMap<>(all.size());
        for (Map.Entry<byte[], byte[]> entry : all.entrySet()) {
            result.put(fromRedisField(entry.getKey()), fromRedisValue(entry.getValue()));
        }
        return result;
    }

    public void hdel(K key, F... fields) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[][] fieldSet = new byte[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            byte[] redisFieldKey = toRedisFieldKey(fields[i]);
            fieldSet[i] = redisFieldKey;
        }

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).hdel(redisKey, fieldSet);
        } else {
            ((BinaryJedisCommands) jedisCommands).hdel(redisKey, fieldSet);
        }
    }

    public boolean hexists(K key, F field) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisFieldKey = toRedisFieldKey(field);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            return ((BinaryJedisClusterCommands) jedisCommands).hexists(redisKey, redisFieldKey);
        } else {
            return ((BinaryJedisCommands) jedisCommands).hexists(redisKey, redisFieldKey);
        }
    }

    public Set<F> hkeys(K key) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        Set<byte[]> fields;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            fields = ((BinaryJedisClusterCommands) jedisCommands).hkeys(redisKey);
        } else {
            fields = ((BinaryJedisCommands) jedisCommands).hkeys(redisKey);
        }

        HashSet<F> result = new HashSet<>(fields.size());
        for (byte[] bField : fields) {
            result.add(fromRedisField(bField));
        }
        return result;
    }

    public Collection<V> hvals(K key) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        Collection<byte[]> values;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            values = ((BinaryJedisClusterCommands) jedisCommands).hvals(redisKey);
        } else {
            values = ((BinaryJedisCommands) jedisCommands).hkeys(redisKey);
        }

        Collection<V> result = new ArrayList<>(values.size());
        for (byte[] bValue : values) {
            result.add(fromRedisValue(bValue));
        }
        return result;
    }

    public void clear(K key) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            BinaryJedisClusterCommands commands = (BinaryJedisClusterCommands) jedisCommands;
            Set<byte[]> fields = commands.hkeys(redisKey);
            commands.hdel(redisKey, fields.toArray(new byte[fields.size()][]));
        } else {
            BinaryJedisCommands commands = (BinaryJedisCommands) jedisCommands;
            Set<byte[]> fields = commands.hkeys(redisKey);
            commands.hdel(redisKey, fields.toArray(new byte[fields.size()][]));
        }
    }

    private byte[] toRedisFieldKey(F field) throws IOException {
        if(field == null) {
            return new byte[0];
        } else if(ReflectionUtils.isPrimitiveOrWrapper(field.getClass())) {
            String sKey = CommonUtils.toString(field);
            return SafeEncoder.encode(sKey);
        } else if(field instanceof byte[]) {
            return (byte[]) field;
        } else {
            return ObjectUtils.serialize(field);
        }
    }

    @SuppressWarnings("unchecked")
    private F fromRedisField(byte[] redisField) throws IOException, ClassNotFoundException {
        if(ReflectionUtils.isPrimitiveOrWrapper(fieldClass)) {
            String sField = SafeEncoder.encode(redisField);
            return (F) CommonUtils.getValueByType(sField, fieldClass);
        } else {
            return ObjectUtils.deserialize(redisField);
        }
    }
}
