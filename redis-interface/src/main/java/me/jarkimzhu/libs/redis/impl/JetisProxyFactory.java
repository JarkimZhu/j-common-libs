package me.jarkimzhu.libs.redis.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.jarkimzhu.libs.redis.IRedis;
import me.jarkimzhu.libs.redis.utils.Attribute;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JetisProxyFactory {
	
//	private static final String  host = "192.168.0.201";
//	private static final String  host = "101.201.116.24";
//	private static final String  host = "10.25.156.27";
//	private static final int port = 6379;

//	private static final int port = 6380;
	
	private static final Logger logger = LoggerFactory.getLogger(JetisProxyFactory.class);

	private static JetisProxyFactory instance = null;
	
	private static Attribute attribute =  new Attribute("/properties/cache.config.properties");

	private JedisPool pool = null;

	private JetisProxyFactory() {
		init();
	}
	
	public static JetisProxyFactory instance() {
		if (instance == null) {
			instance = new JetisProxyFactory();
		}
		return instance;
	}
	
	private void init(){
		logger.warn("初始化 redis connection ");
		
		JedisPoolConfig config = new JedisPoolConfig();

		
		config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
		config.setJmxEnabled(true);
		config.setJmxNamePrefix("RedisPool");
		
		config.setLifo(true);
		
		config.setMaxTotal(200);
		config.setMaxIdle(200);
		config.setMinIdle(200);
		
		config.setMaxWaitMillis(1000*100);	//当borrow一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
		config.setMinEvictableIdleTimeMillis(1800000);	 
		config.setMinIdle(0);
		config.setNumTestsPerEvictionRun(3);
		config.setSoftMinEvictableIdleTimeMillis(1800000);
//		config.setTestOnBorrow(false);
//		config.setTestWhileIdle(false);

		//在borrow一个jedis实例时，进行validate操作；如果为true，则得到的jedis实例均是可用的；  
        config.setTestOnBorrow(true);  
        config.setTestWhileIdle(true);
        

//		config.setBlockWhenExhausted(true);
        
		config.setTimeBetweenEvictionRunsMillis(-1);

		String _host = attribute.value("redis.global.host");
		int _port = attribute.getInt("redis.global.port");
		logger.warn("--------------------------------------Redis Conenection to [" + _host + " :" + _port + "]");
		int port = 6379 ;
		
		if( _port > 0){
			port = _port;
		}
		
		pool = new JedisPool(config, _host , port , 10000);
	}



	/**
	 * 返回指定 的Jedis 对象
	 * 
	 * @return
	 */
	public IRedis getJedis(String setName) {
//		Jedis jedis = pool.getResource();
		if(pool!=null){
			return new IRedis( pool );
		}else{
			return null;
		}
	}
}
