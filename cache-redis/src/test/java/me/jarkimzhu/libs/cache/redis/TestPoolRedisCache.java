package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.redis.pool.PoolRedisCache;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
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
public class TestPoolRedisCache {

    private PoolRedisCache<Integer, User> cache;

    public TestPoolRedisCache() {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(config, "10.2.108.4", 7002, Protocol.DEFAULT_TIMEOUT, "123456");
        cache = new IntegerObjectCache(jedisPool);
    }

    @Test
    public void testSize() throws InterruptedException {
        System.out.println(cache.size());
        cache.put(1, new User());
        System.out.println(cache.get(1));
        System.out.println(cache.size());
        cache.remove(1);
        System.out.println(cache.size());
    }

    @Test
    public void testPutIfAbsent() {
        System.out.println(cache.putIfAbsent(1, new User()));
        System.out.println(cache.putIfAbsent(1, new User()));
    }

    @Test
    public void testKeySet() {
        Set<Integer> keys = cache.keySet();
        System.out.println(keys);
        Assert.assertEquals(keys.size(), cache.size());
        System.out.println(keys.size());
        System.out.println(cache.size());
    }

    @Test
    public void testValues() {
        System.out.println(cache.values());
    }

    @Test
    public void testInfo() {
        JedisPool jedisPool = new JedisPool("10.1.101.194", 7000);
        Jedis jedis = jedisPool.getResource();
        System.out.println(jedis.keys("TGT*zhujiaqi*"));
//        System.out.println(jedis.info());
    }

    private class IntegerObjectCache extends PoolRedisCache<Integer, User> {

        public IntegerObjectCache(JedisPool jedisPool) {
            super("JarkimZhu", jedisPool);
            setTimeout(5000);
        }
    }

    public static final class User implements Serializable {
        private String name;
        private int age;
        private Date birthday;
    }
}
