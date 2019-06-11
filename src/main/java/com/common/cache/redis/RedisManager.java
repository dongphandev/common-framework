package com.common.cache.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

public class RedisManager {

	public static void main(String[] args) {
		Jedis jedis = new Jedis();
		
//		jedis.sadd("nicknames", "nickname#1");
//		jedis.sadd("nicknames", "nickname#2");
//		jedis.sadd("nicknames", "nickname#1");
//		jedis.sadd("nicknames", "nickname#3");
//		jedis.sadd("nicknames", "nickname#4");
		jedis.lpush("l2", "List");
		Set<String> s = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			jedis.lpush("l2", Integer.toString(i));
		}

		
		
//		
//		
//		for(int i=0; i < 1000000; i++) {
//			
//		}
		 
		
		
	}
}
