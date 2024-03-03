package org.playground.ws;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.playground.ws.dto.TemplateDto;
import org.playground.ws.repository.TemplateRepository;

import java.util.HashMap;
import java.util.List;

@Path("/templates")
@RequestScoped
public class TemplatesResource {
    @Inject
    private TemplateRepository templateRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap getTemplates() {
        final List templates = templateRepository
                .findAll()
                .stream()
                .map(t -> new TemplateDto(t))
                .toList();
        final HashMap<String, List> res = new HashMap<>();
        res.put("templates", templates);
        return res;
    }
}
