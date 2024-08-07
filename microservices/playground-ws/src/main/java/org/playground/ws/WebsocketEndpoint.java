package org.playground.ws;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.playground.ws.dto.PlaygroundDto;
import org.playground.ws.factory.PlaygroundFactory;
import org.playground.ws.services.EvaluatorService;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.playground.ws.utils.adapters.LocaleDateTimeTypeAdapter;

@ServerEndpoint(value = "/api/websocket")
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
        final PlaygroundDto playground = PlaygroundFactory.getPlaygroundFromWsEvalRequest(message);
        LOGGER.info("playground to be evaluated with values: " + playground);
        // TODO: Notice that evaluator service is too optimistic. Handle errors properly
        final EvalResponse evalRes = evaluatorService.evaluate(playground);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocaleDateTimeTypeAdapter())
                .create();
        session.getBasicRemote().sendObject(gson.toJson(evalRes));
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
