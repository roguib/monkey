package org.playground.ws.factory;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.ws.rs.NotFoundException;
import org.playground.ws.Playground;
import org.playground.ws.WebsocketEndpoint;
import org.playground.ws.services.CacheServiceImpl;
import redis.clients.jedis.JedisPooled;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class PlaygroundFactory {
    private static final Logger LOGGER = Logger.getLogger(WebsocketEndpoint.class.getName());
    public static Playground getPlayground() {
        // generate new playground object with a unique id
        final Playground playground = new Playground(generatePlaygroundUniqueId());

        // save the object in the cache, so we can start storing eval results
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
        Gson gson = new Gson();
        String json = gson.toJson(playground);
        jedis.set(playground.getId(), json);

        LOGGER.info("New playground created: " + playground);
        return playground;
    }

    public static Playground getPlayground(final String playgroundId) {
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
        String playgroundJson = jedis.get(playgroundId);

        if (playgroundJson == null) {
            throw new NotFoundException();
        }

        JsonReader jsonReader = Json.createReader(new StringReader(playgroundJson));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        final Playground playground = new Playground(
            playgroundId,
            object.getJsonArray("history").getValuesAs(JsonString::getString)
        );
        LOGGER.info("New playground created: " + playground);
        return playground;
    }

    public static String generatePlaygroundUniqueId() {
        // generate initial id
        String id = PlaygroundFactory.generateId();
        // ensure the id is not in the Redis cache, otherwise generate another one
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
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
