package org.playground.ws.services;

import java.io.StringReader;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import io.helidon.common.config.ConfigValue;
import io.helidon.config.Config;
import jakarta.json.JsonReader;
import javassist.NotFoundException;
import org.playground.ws.EvalResponse;
import org.playground.ws.Playground;
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

    public EvalResponse evaluate(final Playground playground) throws NotFoundException {
        final String playgroundId = playground.getId();
        final JedisPooled jedis = CacheServiceImpl.getCacheConnection();
        if (jedis.get(playgroundId) == null) {
            LOGGER.info("Attempting to evaluate a program on a playground id that doesn't exist. playgroundId: "
                    + playgroundId);
            throw new NotFoundException("The playground identified by " + playgroundId + " was not found");
        }

        LOGGER.info("About to evaluate program: " + playground.getProgram());
        JsonObject programJson = JSON_BUILDER.createObjectBuilder()
                .add("rawProgram", playground.getProgram())
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
            final String jsonEvalRes = response.as(String.class);
            // todo: use a mapper json -> java object
            JsonReader jsonReader = Json.createReader(new StringReader(jsonEvalRes));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();

            final EvalResponse evalRes = new EvalResponse(object.getString("result"), object.getString("status"));
            LOGGER.info("POST request to evaluate service executed with response: " + evalRes);
            playground.addHistoryResult(evalRes.getResult());

            Gson gson = new Gson();
            String json = gson.toJson(playground);
            jedis.set(playground.getId(), json);

            return evalRes;
        }
    }
}
