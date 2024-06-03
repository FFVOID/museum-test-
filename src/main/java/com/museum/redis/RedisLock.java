package com.museum.redis;

import java.util.Collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisLock {
	
	private static final long LOCK_EXPIRE = 30000; //30초
	
	public boolean lock(String key, String value) {
		try (Jedis jedis = RedisClient.getJedis()){
			SetParams params = new SetParams();
			params.nx().px(LOCK_EXPIRE);
			String result = jedis.set(key, value, params);
			return "OK".equals(result);
		} catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
	public void unlock(String key, String value) {
		try(Jedis jedis = RedisClient.getJedis()){
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
					+ "return redis.call('del', KEYS[1]) else return 0 end";
			jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
		} catch (Exception e) {
            e.printStackTrace(); 
        }
	}
}
