package org.playground.ws;

public class EvalRequest {
    private Playground playground;
    private String program;

    public EvalRequest(final Playground playground, final String program) {
        this.playground = playground;
        this.program = program;
    }

    public Playground getPlayground() {
        return playground;
    }

    public void setPlayground(Playground playground) {
        this.playground = playground;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "playgroundId: " + playground.getId() + "\n" + program;
    }
}
