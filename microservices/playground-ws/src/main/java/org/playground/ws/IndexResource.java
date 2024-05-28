package org.playground.ws;

import io.helidon.common.configurable.Resource;
import io.helidon.microprofile.cors.CrossOrigin;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * A single endpoint that returns the generated index.html file after building the frontend. The index.html
 * contains the necessary links and scripts to download the extra js and css files, along with some images.
 */
@Path("/")
@RequestScoped
public class IndexResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getPlayground() {
        return Resource.create("WEB/index.html").string();
    }

    /**
     * Enable CORS
     * There's currently a bug in Helidon MP that doesn't consider port when matching origins
     * https://github.com/helidon-io/helidon/pull/8166
     */
    @OPTIONS
    @CrossOrigin(value = {"https://www.roguib.com"})
    public void optionsForIndex() {}
}
