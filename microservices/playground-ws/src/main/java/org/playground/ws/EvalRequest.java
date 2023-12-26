package org.playground.ws;

import jakarta.json.JsonObject;

public class EvalRequest {
    private static final String PLAYGROUND_ID = "playgroundId";
    private static final String PROGRAM = "program";

    public EvalRequest(final JsonObject object) {
        this.playgroundId = object.getString("playgroundId");
        this.program = object.getString("program");
    }
    private String playgroundId;
    private String program;

    public String getPlaygroundId() {
        return playgroundId;
    }

    public void setPlaygroundId(String playgroundId) {
        this.playgroundId = playgroundId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "playgroundId: " + playgroundId + "\n" + program;
    }
}
