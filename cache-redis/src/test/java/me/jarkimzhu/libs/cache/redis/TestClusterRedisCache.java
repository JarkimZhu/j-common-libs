package me.jarkimzhu.libs.cache.redis;

import org.junit.Test;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2017/4/21.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class TestClusterRedisCache {
    private ClusterRedisCache<String, String> cache;

    public TestClusterRedisCache() {
        JedisPoolConfig config = new JedisPoolConfig();
        HostAndPort hp1 = new HostAndPort("10.2.108.4", 7000);
        HostAndPort hp2 = new HostAndPort("10.2.108.4", 7001);
        Set<HostAndPort> hps = new HashSet<>(2);
        hps.add(hp1);
        hps.add(hp2);
        JedisCluster jedisCluster = new JedisCluster(hps, 500, 1000, 3, "123456", config);
        cache = new ClusterRedisCache<>(jedisCluster, String.class, String.class);
    }

    @Test
    public void testSize() {
        cache.put("JarkimZhu", "haha");
        System.out.println(cache.containsKey("JarkimZhu"));
        System.out.println(cache.get("JarkimZhu"));
        System.out.println(cache.size());
        cache.remove("JarkimZhu");
        System.out.println(cache.size());
    }

    @Test
    public void testKeys() {
        System.out.println(cache.keySet().size());
        System.out.println(cache.size());
    }

    @Test
    public void testWrite() {
        cache.put("JarkimZhu", "hello");
        cache.remove("JarkimZhu");
    }

    @Test
    public void testValues() {
        System.out.println(cache.values().size());
    }
}