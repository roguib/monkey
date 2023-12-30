package org.playground.ws;

import io.helidon.common.configurable.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Manages the lifecycle of a playground
 */
@Path("/")
@RequestScoped
public class IndexResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getPlayground() {
        return Resource.create("WEB/index.html").string();
    }
}
