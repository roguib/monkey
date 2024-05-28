package org.playground.ws;

import io.helidon.microprofile.cors.CrossOrigin;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.playground.ws.dto.CreatePlaygroundDto;
import org.playground.ws.factory.PlaygroundFactory;
import org.playground.ws.repository.TemplateRepository;

/**
 * Manages the lifecycle of a playground
 */
@Path("/playground")
@RequestScoped
public class PlaygroundResource {
    @Inject
    private TemplateRepository templateRepository;
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequestBody(name = "templateId",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, requiredProperties = { "templateId" })))
    public Playground createPlayground(final CreatePlaygroundDto createPlaygroundDto) {
        final PlaygroundFactory playgroundFactory = new PlaygroundFactory(templateRepository);
        return playgroundFactory.getPlayground(createPlaygroundDto);
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
    @Path("/new")
    @CrossOrigin(value = {"https://www.roguib.com"}, allowMethods = {HttpMethod.POST})
    public void optionsForNewPlayground() {}

    @OPTIONS
    @Path("/{playgroundId}")
    @CrossOrigin(value = {"https://www.roguib.com"}, allowMethods = {HttpMethod.GET})
    public void optionsForGetPlayground() {}
}
