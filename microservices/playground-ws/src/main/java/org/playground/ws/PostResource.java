package org.playground.ws;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.playground.ws.dao.Post;
import org.playground.ws.repository.PostRepository;

import java.util.HashMap;

@Path("/post")
@RequestScoped
public class PostResource {
    @Inject
    private PostRepository postRepository;

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap createPost() {
        final Post p = Post.of("The first post in DB", "lorem ipsum");
        postRepository.save(p);
        final HashMap<String, String> resp = new HashMap<>();
        resp.put("created", "ok");
        return resp;
    }
}
