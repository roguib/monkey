package org.playground.ws.factory;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.ws.rs.NotFoundException;
import org.playground.ws.dto.PlaygroundDto;
import org.playground.ws.dao.TemplateDao;
import org.playground.ws.dto.CreatePlaygroundDto;
import org.playground.ws.repository.TemplateRepository;
import org.playground.ws.services.CacheServiceImpl;
import redis.clients.jedis.JedisPooled;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

public class PlaygroundFactory {
    private static final Logger LOGGER = Logger.getLogger(PlaygroundFactory.class.getName());
    private TemplateRepository templateRepository;

    public PlaygroundFactory(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public PlaygroundDto getPlayground(final CreatePlaygroundDto createPlaygroundDto) {
        // first check if we have to create a new playground from a template
        final String templateId = createPlaygroundDto.getTemplateId();
        Optional<TemplateDao> templateDao = Optional.empty();
        if (templateId != null) {
            templateDao = this.templateRepository.findById(templateId);
        }

        if (templateDao.isEmpty() && templateId != null) {
            throw new NotFoundException();
        }

        // generate new playground object with a unique id
        final PlaygroundDto playground = new PlaygroundDto(generatePlaygroundUniqueId());

        // if the user has selected a template, init the playground with the corresponding code
        if (templateDao.isPresent()) {
            playground.setProgram(templateDao.get().getProgram());
        }

        // save the object in the cache, so we can start storing eval results
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
        Gson gson = new Gson();
        String json = gson.toJson(playground);
        jedis.set(playground.getId(), json);

        LOGGER.info("New playground created: " + playground);
        return playground;
    }

    public static PlaygroundDto getPlayground(final String playgroundId) {
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
        String playgroundJson = jedis.get(playgroundId);

        if (playgroundJson == null) {
            throw new NotFoundException();
        }

        JsonReader jsonReader = Json.createReader(new StringReader(playgroundJson));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        final PlaygroundDto playground = new PlaygroundDto(
            playgroundId,
            object.getString("program"),
            object.getJsonArray("history").getValuesAs(JsonString::getString)
        );
        LOGGER.info("New playground created: " + playground);
        return playground;
    }

    public static PlaygroundDto getPlaygroundFromWsEvalRequest(final String jsonObject) {
        LOGGER.info("Getting playground from json object: " + jsonObject);
        JsonReader jsonReader = Json.createReader(new StringReader(jsonObject));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        final String playgroundId = object.getString("playgroundId");
        final String program = object.getString("program");

        final PlaygroundDto playground = PlaygroundFactory.getPlayground(playgroundId);
        playground.setProgram(program);
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
