package org.playground.ws;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.playground.ws.dao.Template;
import org.playground.ws.repository.TemplateRepository;

import java.util.HashMap;

@Path("/template")
@RequestScoped
public class TemplateResource {
    @Inject
    private TemplateRepository templateRepository;

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap createTemplate() {
        final Template p = Template.of("Template title", "Template description", "let a = 1;");
        templateRepository.save(p);
        final HashMap<String, String> resp = new HashMap<>();
        resp.put("created", "ok");
        return resp;
    }
}
