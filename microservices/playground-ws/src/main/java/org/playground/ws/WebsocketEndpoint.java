package org.playground.ws;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import org.playground.ws.services.EvaluatorService;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.json.JsonObject;

@ServerEndpoint(value = "/websocket")
public class WebsocketEndpoint {
    private static final Logger LOGGER = Logger.getLogger(WebsocketEndpoint.class.getName());

    @Inject
    private EvaluatorService evaluatorService;

    /**
     * OnOpen call.
     *
     * @param session The websocket session.
     * @throws IOException If error occurs.
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        LOGGER.info("OnOpen called");
    }

    /**
     * OnMessage call.
     *
     * @param session The websocket session.
     * @param message The message received.
     * @throws Exception If error occurs.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        LOGGER.info("Message: " + message);
        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        final EvalRequest evalRequest = new EvalRequest(object);
        // TODO: Notice that evaluator service is too optimistic. Handle errors properly
        final String evalRes = evaluatorService.evaluate(evalRequest);
        session.getBasicRemote().sendObject(evalRes);
    }

    /**
     * OnError call.
     *
     * @param t The throwable.
     */
    @OnError
    public void onError(Throwable t) {
        LOGGER.info("OnError called: " + t.getLocalizedMessage());
    }

    /**
     * OnError call.
     *
     * @param session The websocket session.
     */
    @OnClose
    public void onClose(Session session) {
        LOGGER.info("OnClose called");
    }
}
