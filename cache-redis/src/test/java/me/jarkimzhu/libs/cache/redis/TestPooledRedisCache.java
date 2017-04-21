package me.jarkimzhu.libs.cache.redis;

import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created on 2017/4/14.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class TestPooledRedisCache {

    private PooledRedisCache<Integer, User> cache;

    public TestPooledRedisCache() {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(config, "10.2.108.4", 7001, Protocol.DEFAULT_TIMEOUT, "123456");
        cache = new IntegerObjectCache(jedisPool);
    }

    @Test
    public void testSize() {
        System.out.println(cache.size());
        cache.put(1, new User());
        System.out.println(cache.get(1));
        System.out.println(cache.size());
        cache.remove(1);
        System.out.println(cache.size());
    }

    @Test
    public void testKeySet() {
        Set<Integer> keys = cache.keySet();
        System.out.println(keys);
        System.out.println(keys.size());
        System.out.println(cache.size());
    }

    @Test
    public void testValues() {
        cache.values();
    }

    private class IntegerObjectCache extends PooledRedisCache<Integer, User> {

        public IntegerObjectCache(JedisPool jedisPool) {
            super(jedisPool);
        }
    }

    public static final class User implements Serializable {
        private String name;
        private int age;
        private Date birthday;
    }
}
