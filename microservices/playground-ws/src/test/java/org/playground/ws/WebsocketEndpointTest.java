package org.playground.ws;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import io.helidon.microprofile.server.ServerCdiExtension;
import io.helidon.microprofile.testing.junit5.HelidonTest;

import jakarta.inject.Inject;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;
import org.junit.jupiter.api.Test;
import org.playground.ws.services.EvaluatorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@HelidonTest
class WebsocketEndpointTest {
    private static final Logger LOGGER = Logger.getLogger(WebsocketEndpoint.class.getName());
    private static final String RAW_PROGRAM = "let a = 1; a;";
    private static final String EXPECTED_EVAL_RESULT = "1";

    @Inject
    private ServerCdiExtension server;
    private final ClientManager websocketClient = ClientManager.createClient(JdkClientContainer.class.getName());


    @Test
    public void testPlayground() throws IOException, DeploymentException {
        EvaluatorService evaluatorServiceMocked = mock(EvaluatorService.class);
        when(evaluatorServiceMocked.evaluate(RAW_PROGRAM)).thenReturn("1");

        URI websocketUri = URI.create("ws://localhost:" + server.port() + "/websocket");
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

        websocketClient.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig EndpointConfig) {
                try {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            final String evalResult = evaluatorServiceMocked.evaluate(message);
                            assertEquals(evalResult, EXPECTED_EVAL_RESULT);
                        }
                    });

                    session.getBasicRemote().sendText(RAW_PROGRAM);
                } catch (IOException e) {
                    fail("Unexpected exception " + e);
                }
            }

            @Override
            public void onClose(Session session, CloseReason closeReason) {
                LOGGER.info("Client OnClose called '" + closeReason + "'");
            }

            @Override
            public void onError(Session session, Throwable thr) {
                LOGGER.info("Client OnError called '" + thr + "'");

            }
        }, config, websocketUri);
    }
}
