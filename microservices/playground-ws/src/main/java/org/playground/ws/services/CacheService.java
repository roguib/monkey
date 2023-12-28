package org.playground.ws.services;

import redis.clients.jedis.JedisPooled;

public interface CacheService {
    static JedisPooled getCacheConnection() {
        return null;
    }
}
