package org.playground.ws.utils;

import redis.clients.jedis.JedisPooled;

import java.util.HashMap;

public class JedisPooledMocked extends JedisPooled {
    public HashMap<String, String> map = new HashMap<>();
    @Override
    public String set(String key, String value) {
        return "Jedis set operation correctly executed";
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    public void setValueForKey(String key, String value) {
        map.put(key, value);
    }
}
