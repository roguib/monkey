
package org.monkey.ws;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.metrics.api.MetricsFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import jakarta.json.JsonObject;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@HelidonTest
class MainTest {

    @Inject
    private MetricRegistry registry;

    @Inject
    private WebTarget target;

    @BeforeAll
    static void clear() {
        MetricsFactory.closeAll();
    }

    @Test
    void testHealth() {
        Response response = target
                .path("health")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    void testEvaluateSuccessfulResult() {
        final Response r = target
                .path("evaluate")
                .request()
                .post(Entity.entity("{\"rawProgram\" : \"let a = 1; a;\"}", MediaType.APPLICATION_JSON));
        assertThat(r.getStatus(), is(200));

        String output = r.readEntity(String.class);
        final JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(output))) {
            json = reader.readObject();
        }
        String status = json.getString("status");
        assertEquals(status, "ok");
        String result = json.getString("result");
        assertEquals(result, "1");
    }

    @Test
    void testErrorOnParse() {
        final Response r = target
                .path("evaluate")
                .request()
                .post(Entity.entity("{\"rawProgram\" : \"5 +-/ 7;\"}", MediaType.APPLICATION_JSON));
        assertThat(r.getStatus(), is(200));

        String output = r.readEntity(String.class);
        final JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(output))) {
            json = reader.readObject();
        }
        String status = json.getString("status");
        assertEquals(status, "error");
        String result = json.getString("result");
        assertEquals(result, "One or more errors have occurred while parsing the program. Aborting");
    }

    @Test
    void testErrorOnEvaluate() {
        final Response r = target
                .path("evaluate")
                .request()
                .post(Entity.entity("{\"rawProgram\" : \"5 + true;\"}", MediaType.APPLICATION_JSON));
        assertThat(r.getStatus(), is(200));

        String output = r.readEntity(String.class);
        final JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(output))) {
            json = reader.readObject();
        }
        String status = json.getString("status");
        assertEquals(status, "error");
        String result = json.getString("result");
        assertEquals(result, "Error while evaluating the program: type mismatch: INTEGER + BOOLEAN");
    }
}
