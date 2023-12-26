package org.playground.ws.services;

import java.util.Map;
import java.util.logging.Logger;

import io.helidon.common.config.ConfigValue;
import io.helidon.config.Config;
import javassist.NotFoundException;
import org.playground.ws.EvalRequest;
import org.playground.ws.WebsocketEndpoint;
import io.helidon.webclient.api.HttpClientResponse;
import io.helidon.webclient.api.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import redis.clients.jedis.JedisPooled;

@ApplicationScoped
public class EvaluatorService {

    private static final Logger LOGGER = Logger.getLogger(WebsocketEndpoint.class.getName());
    private static final JsonBuilderFactory JSON_BUILDER = Json.createBuilderFactory(Map.of());

    public String evaluate(EvalRequest evalRequest) throws NotFoundException {
        final CacheServiceImpl<JedisPooled> cacheService = new CacheServiceImpl<>();
        final JedisPooled jedis = cacheService.getCacheConnection();
        if (jedis.get(evalRequest.getPlaygroundId()) == null) {
            LOGGER.info("Attempting to evaluate a program on a playground id that doesn't exist. playgroundId: "
                    + evalRequest.getPlaygroundId());
            throw new NotFoundException("The playground identified by " + evalRequest.getPlaygroundId() + " was not found");
        }

        LOGGER.info("About to evaluate program: " + evalRequest.getProgram());
        JsonObject programJson = JSON_BUILDER.createObjectBuilder()
                .add("rawProgram", evalRequest.getProgram())
                .build();
        LOGGER.info("JsonObject successfully created: " + programJson.getString("rawProgram"));

        Config config = Config.create();
        ConfigValue<String> evaluatorUri = config.get("webservices.monkey-ws.uri").asString();
        if (!evaluatorUri.isPresent()) {
            throw new IllegalStateException("Unable to retrieve uri for monkey-ws");
        }

        WebClient client = WebClient.builder()
                .baseUri(evaluatorUri.get())
                .build();
        ConfigValue<String> endpoint = config.get("webservices.monkey-ws.endpoints.evaluate").asString();
        if (!endpoint.isPresent()) {
            throw new IllegalStateException("Unable to retrieve endpoint for monkey-ws evaluator service");
        }

        try (HttpClientResponse response = client.post(endpoint.get()).submit(programJson)) {
            final String evalRes = response.as(String.class);
            LOGGER.info("POST request to evaluate service executed with response: " + evalRes);
            return evalRes;
        }
    }
}
