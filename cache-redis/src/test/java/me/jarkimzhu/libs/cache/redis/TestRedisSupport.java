package me.jarkimzhu.libs.cache.redis;

import me.jarkimzhu.libs.cache.redis.cluster.ClusterRedisCache;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2017/4/24.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class TestRedisSupport {
    private RedisSupport<User, String> support;
    private ClusterRedisCache<String, String> cache;

    public TestRedisSupport() {
        JedisPoolConfig config = new JedisPoolConfig();
        HostAndPort hp1 = new HostAndPort("10.2.108.4", 7000);
        HostAndPort hp2 = new HostAndPort("10.2.108.4", 7001);
        Set<HostAndPort> hps = new HashSet<>(2);
        hps.add(hp1);
        hps.add(hp2);
        JedisCluster jedisCluster = new JedisCluster(hps, 500, 1000, 3, "123456", config);
        cache = new ClusterRedisCache<>("TGT", jedisCluster, String.class, String.class);
        support = new RedisSupport<>("TGT", User.class, String.class);
    }

    @Test
    public void testKeys() {
        System.out.println(cache.keySet());
    }

    private static class User implements Serializable {
        private int age;
        private String name;
        private Date birth;

        public User(int age, String name) {
            this.age = age;
            this.name = name;
            this.birth = new Date();
        }

        @Override
        public String toString() {
            return name + " " + age + " " + birth.toLocaleString();
        }
    }
}
