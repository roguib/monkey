package org.playground.ws.services;

import io.helidon.common.config.ConfigValue;
import io.helidon.config.Config;
import redis.clients.jedis.JedisPooled;

public class CacheServiceImpl implements CacheService {
    private static final String CACHE_HOST_CONFIG = "cache.host";
    private static final String CACHE_PORT_CONFIG = "cache.port";

    public static JedisPooled getCacheConnection() {
        Config config = Config.create();
        ConfigValue<String> cacheHost = config.get(CACHE_HOST_CONFIG).asString();
        if (!cacheHost.isPresent()) {
            throw new IllegalStateException("Unable to retrieve host for cache connection");
        }
        ConfigValue<Integer> cachePort = config.get(CACHE_PORT_CONFIG).asInt();
        if (!cachePort.isPresent()) {
            throw new IllegalStateException("Unable to retrieve port for cache connection");
        }
        return new JedisPooled(cacheHost.get(), cachePort.get());
    }
}
