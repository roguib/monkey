package org.playground.ws.factory;

import com.google.common.hash.Hashing;
import org.playground.ws.Playground;
import org.playground.ws.WebsocketEndpoint;
import org.playground.ws.services.CacheServiceImpl;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.schemafields.TextField;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class PlaygroundFactory {
    private static final Logger LOGGER = Logger.getLogger(WebsocketEndpoint.class.getName());
    public static Playground getPlayground() {
        // generate new playground object with a unique id
        final Playground playground = new Playground(PlaygroundFactory.generatePlaygroundUniqueId());

        // save the object in the cache, so we can start storing eval results
        final CacheServiceImpl<JedisPooled> cacheService = new CacheServiceImpl<>();
        JedisPooled jedis = cacheService.getCacheConnection();
        jedis.set(playground.getId(), playground.getHistory().toString());

        return playground;
    }

    private static String generatePlaygroundUniqueId() {
        // generate initial id
        String id = PlaygroundFactory.generateId();
        // ensure the id is not in the Redis cache, otherwise generate another one
        final CacheServiceImpl<JedisPooled> cacheService = new CacheServiceImpl<>();
        JedisPooled jedis = cacheService.getCacheConnection();
        while (jedis.get(id) != null) {
            id = PlaygroundFactory.generateId();
        }
        LOGGER.info("Unique playground id found with value: " + id);
        return id;
    }

    private static String generateId() {
        // hash the current date, so we have less probability to generate an id
        // that collides with an existing id
        String sha256hex = Hashing.sha256()
                .hashString(new Date().toString(), StandardCharsets.UTF_8)
                .toString();
        LOGGER.info("sha256hex generated: " + sha256hex);
        // give correct format [a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}
        StringBuilder result = new StringBuilder("");
        result.append(sha256hex.substring(0, 3) + "-");
        result.append(sha256hex.substring(3, 6) + "-");
        result.append(sha256hex.substring(6, 9));
        LOGGER.info("semi-unique playground id generated: " + result);
        return result.toString();
    }
}
