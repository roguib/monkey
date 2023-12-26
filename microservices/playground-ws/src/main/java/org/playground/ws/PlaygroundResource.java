package org.playground.ws;

import io.helidon.microprofile.cors.CrossOrigin;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.playground.ws.factory.PlaygroundFactory;

/**
 * Manages the lifecycle of a playground
 */
@Path("/playground")
@RequestScoped
public class PlaygroundResource {
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Playground createPlayground() {
        return PlaygroundFactory.getPlayground();
    }

    @GET
    @Path("/{playgroundId}")
    public Playground getPlayground(@PathParam("playgroundId") String playgroundId) {
        return PlaygroundFactory.getPlayground(playgroundId);
    }

    /**
     * Enable CORS for development
     * There's currently a bug in Helidon MP that doesn't consider port when matching origins
     * https://github.com/helidon-io/helidon/pull/8166
     */
    @OPTIONS
    @CrossOrigin(value = {"http://localhost:3000/"})
    public void optionsForPlayground() {}
}
