package test;

import redis.clients.jedis.Jedis;

public class CleanToken {
	private static Jedis  jedis = new Jedis ("192.168.0.201",6380);

	
	public static void main( String args[] ){
		jedis.flushAll();
	}
}
