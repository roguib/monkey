package org.playground.ws.factory;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.playground.ws.EvalRequest;

import java.io.StringReader;

public class EvalRequestFactory {
    public static EvalRequest getEvalRequest(final String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        final String playgroundId = object.getString("playgroundId");
        final String program = object.getString("program");

        return new EvalRequest(
                PlaygroundFactory.getPlayground(playgroundId),
                program
        );
    }
}
