package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.utils.CommonUtils;
import me.jarkimzhu.libs.utils.ObjectUtils;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class RedisSupport<K extends Serializable, V extends Serializable> implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RedisSupport.class);
    private static final String NS_SEP = ":";

    private String namespace;
    private byte[] bNamespace;
    private Class<K> keyClass;
    private Class<V> valueClass;

    protected ThreadLocal<JedisCommands> local = new ThreadLocal<>();

    public RedisSupport(String namespace, Class<K> keyClass, Class<V> valueClass) {
        if(namespace != null) {
            this.namespace = namespace + NS_SEP;
            this.bNamespace = SafeEncoder.encode(this.namespace);
        }

        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) throws IOException, ClassNotFoundException {
        byte[] redisKey = toRedisKey(key);
        byte[] redisValue = getRedisValueFromJedisCommands(redisKey);
        return fromRedisValue(redisValue);
    }

    private byte[] getRedisValueFromJedisCommands(byte[] redisKey) {
        JedisCommands jedisCommands = getJedisCommands();
        byte[] redisValue;
        if(jedisCommands instanceof BinaryJedisClusterCommands) {
            redisValue = ((BinaryJedisClusterCommands) jedisCommands).get(redisKey);
        } else {
            redisValue = ((BinaryJedisCommands) jedisCommands).get(redisKey);
        }
        return redisValue;
    }

    public void set(K key, V value) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisValue = toRedisValue(value);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).set(redisKey, redisValue);
        } else {
            ((BinaryJedisCommands) jedisCommands).set(redisKey, redisValue);
        }
    }
    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
     * GB).
     * @param key the key
     * @param value the value
     * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *          if it already exist.
     * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time expire time in the units of <code>expx</code>
     * @return Status code reply
     */
    public int set(K key, V value, String nxxx, String expx, long time) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisValue = toRedisValue(value);
        byte[] bNxxx = SafeEncoder.encode(nxxx);
        byte[] bExpx = SafeEncoder.encode(expx);

        String result;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            BinaryJedisClusterCommands commands = (BinaryJedisClusterCommands) jedisCommands;
            result = commands.set(redisKey, redisValue, bNxxx, bExpx, time);
        } else {
            result = ((BinaryJedisCommands) jedisCommands).set(redisKey, redisValue, bNxxx, bExpx, time);
        }
        return "OK".equalsIgnoreCase(result) ? 1 : 0;
    }

    public void setex(K key, V value, int seconds) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisValue = toRedisValue(value);

        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).setex(redisKey, seconds, redisValue);
        } else {
            ((BinaryJedisCommands) jedisCommands).setex(redisKey, seconds, redisValue);
        }
    }

    public int setnx(K key, V value, int seconds) throws IOException {
        if(seconds > -1) {
            return set(key, value, "NX", "EX", seconds);
        }

        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        byte[] redisValue = toRedisValue(value);

        long result;
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            result = ((BinaryJedisClusterCommands) jedisCommands).setnx(redisKey, redisValue);
        } else {
            result = ((BinaryJedisCommands) jedisCommands).setnx(redisKey, redisValue);
        }
        return (int) result;
    }

    public void expire(K key, int seconds) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        if(jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).expire(redisKey, seconds);
        } else {
            ((BinaryJedisCommands) jedisCommands).expire(redisKey, seconds);
        }
    }

    public boolean exists(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();
        byte[] redisKey = toRedisKey(key);
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            return ((BinaryJedisClusterCommands) jedisCommands).exists(redisKey);
        } else {
            return ((BinaryJedisCommands) jedisCommands).exists(redisKey);
        }
    }

    public void del(K key) throws IOException {
        JedisCommands jedisCommands = getJedisCommands();

        byte[] redisKey = toRedisKey(key);
        if (jedisCommands instanceof BinaryJedisClusterCommands) {
            ((BinaryJedisClusterCommands) jedisCommands).del(redisKey);
        } else {
            ((BinaryJedisCommands) jedisCommands).del(redisKey);
        }
    }

    @SuppressWarnings("unchecked")
    public<E extends Serializable> Set<K> keys(E pattern) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();
        if(jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;

            byte[] redisKey = toRedisKey(pattern);
            Set<byte[]> keys = jedis.keys(redisKey);
            HashSet<K> keySet = new HashSet<>(keys.size());
            for (byte[] bytes : keys) {
                keySet.add(fromRedisKey(bytes));
            }
            return keySet;
        } else {
            throw new JedisClusterException("JedisCluster not support this operation");
        }
    }

    public <E extends Serializable> Map<K, V> query(E param) throws IOException, ClassNotFoundException {
        JedisCommands jedisCommands = getJedisCommands();
        if(jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;

            byte[] redisKey = toRedisKey(param);
            Set<byte[]> bKeys = jedis.keys(redisKey);

            HashMap<Integer, HashSet<byte[]>> slotMap = new HashMap<>();
            for(byte[] bytes : bKeys) {
                int slot = JedisClusterCRC16.getSlot(bytes);
                HashSet<byte[]> slotKeys = slotMap.computeIfAbsent(slot, k1 -> new HashSet<>());
                slotKeys.add(bytes);
            }

            HashMap<K, V> result = new HashMap<>(bKeys.size());
            for(HashSet<byte[]> keySet : slotMap.values()) {
                int size = keySet.size();
                byte[][] keys = keySet.toArray(new byte[size][]);
                byte[][] values = jedis.mget(keys).toArray(new byte[size][]);
                for(int i = 0; i < size; i++) {
                    byte[] bValue = values[i];
                    try {
                        result.put(fromRedisKey(keys[i]), fromRedisValue(bValue));
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
            return result;
        } else {
            throw new JedisClusterException("JedisCluster not support this operation");
        }
    }

    public Object eval(String script) {
        JedisCommands jedisCommands = getJedisCommands();
        if (jedisCommands instanceof BinaryScriptingCommands) {
            return ((BinaryScriptingCommands) jedisCommands).eval(SafeEncoder.encode(script));
        } else {
            throw new JedisClusterException("JedisCluster not support this operation");
        }
    }

    public Object eval(String script, List<K> keys, List<V> args) throws IOException {
        if (CommonUtils.isBlank(keys) || CommonUtils.isBlank(args)) {
            return eval(script);
        }

        JedisCommands jedisCommands = getJedisCommands();
        List<byte[]> bKeys = new ArrayList<>(keys.size());
        List<byte[]> bArgs = new ArrayList<>(args.size());
        for (K key : keys) {
            bKeys.add(toRedisKey(key));
        }
        for (V arg : args) {
            bArgs.add(toRedisValue(arg));
        }
        byte[] bScript = SafeEncoder.encode(script);
        if (jedisCommands instanceof BinaryScriptingCommands) {
            return ((BinaryScriptingCommands) jedisCommands).eval(bScript, bKeys, bArgs);
        } else {
            return ((JedisClusterBinaryScriptingCommands) jedisCommands).eval(bScript, bKeys, bArgs);
        }
    }

    public Collection<V> values() throws IOException, ClassNotFoundException {
        return query("*").values();
    }

    public long size() {
        JedisCommands jedisCommands = getJedisCommands();
        if(jedisCommands instanceof Jedis) {
            if(namespace != null) {
                byte[] redisKey = SafeEncoder.encode(namespace + "*");
                Set<byte[]> keySet = ((Jedis) jedisCommands).keys(redisKey);
                return keySet.size();
            } else {
                return ((Jedis) jedisCommands).dbSize();
            }
        } else {
            throw new JedisClusterException("JedisCluster not support this operation");
        }
    }

    <E extends Serializable> byte[] toRedisKey(E key) throws IOException {
        if(ReflectionUtils.isPrimitiveOrWrapper(key.getClass())) {
            String sKey = CommonUtils.toString(key);
            if(namespace != null) {
                return SafeEncoder.encode(namespace + sKey);
            } else {
                return SafeEncoder.encode(sKey);
            }
        } else if(key instanceof byte[]) {
            return (byte[]) key;
        } else {
            byte[] bKey = ObjectUtils.serialize(key);
            if(bNamespace != null) {
                byte[] redisKey = new byte[bNamespace.length + bKey.length];
                System.arraycopy(bNamespace, 0, redisKey, 0, bNamespace.length);
                System.arraycopy(bKey, 0, redisKey, bNamespace.length, bKey.length);
                return redisKey;
            } else {
                return bKey;
            }
        }
    }

    @SuppressWarnings("unchecked")
    K fromRedisKey(byte[] redisKey) throws IOException, ClassNotFoundException {
        if(ReflectionUtils.isPrimitiveOrWrapper(keyClass)) {
            String sKey = SafeEncoder.encode(redisKey);
            if(namespace != null) {
                sKey = sKey.substring(namespace.length());
            }
            return (K) CommonUtils.getValueByType(sKey, keyClass);
        } else {
            if(namespace != null) {
                byte[] key = new byte[redisKey.length - bNamespace.length];
                System.arraycopy(redisKey, bNamespace.length, key, 0, key.length);
                return ObjectUtils.deserialize(key);
            } else {
                return ObjectUtils.deserialize(redisKey);
            }
        }
    }

    @SuppressWarnings("unchecked")
    V fromRedisValue(byte[] bValue) throws IOException, ClassNotFoundException {
        if(bValue == null) {
            return null;
        } else if(ReflectionUtils.isPrimitiveOrWrapper(valueClass)) {
            return (V) CommonUtils.getValueByType(SafeEncoder.encode(bValue), valueClass);
        } else {
            return ObjectUtils.deserialize(bValue);
        }
    }

    byte[] toRedisValue(V value) throws IOException {
        if (ReflectionUtils.isPrimitiveOrWrapper(value.getClass())) {
            return SafeEncoder.encode(CommonUtils.toString(value));
        } else {
            return ObjectUtils.serialize(value);
        }
    }

    JedisCommands getJedisCommands() {
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
        local.remove();
    }
}
