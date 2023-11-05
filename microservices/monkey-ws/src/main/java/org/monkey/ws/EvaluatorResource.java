
package org.monkey.ws;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.interpreter.repl.ProgramEvaluator;

/**
 * A simple JAX-RS resource to evaluate monkey programs and return the resulting evaluation. Examples:
 *
 * Evaluate a program:
 *  curl -X POST -H "Content-Type: application/json" -d '{"rawProgram" : "let a = 1; a;"}' http://localhost:8080/evaluate
 *
 * Response as JSON object:
 * {"result":"1"}
 */
@Path("/evaluate")
@RequestScoped
public class EvaluatorResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequestBody(name = "rawProgram",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.OBJECT, requiredProperties = { "rawProgram" })))
    public Evaluation evaluateProgram(RawProgram rawProgram) {
        String evaluated;
        Evaluation evaluationResult;

        try {
            evaluated = ProgramEvaluator.evaluate(rawProgram.getRawProgram());
            if (evaluated.contains(ProgramEvaluator.ERROR_PARSE_TOKEN) ||
                    evaluated.contains(ProgramEvaluator.ERROR_EVAL_TOKEN)) {
                evaluationResult = new Evaluation(evaluated, Evaluation.ERROR);
            } else {
                evaluationResult = new Evaluation(evaluated);
            }
        } catch (Exception error) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            evaluationResult = new Evaluation("", Evaluation.ERROR);
            return evaluationResult;
        }

        Response.status(Response.Status.OK);
        return evaluationResult;
    }
}
