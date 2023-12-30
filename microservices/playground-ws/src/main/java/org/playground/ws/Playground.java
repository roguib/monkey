package org.playground.ws;

import java.util.ArrayList;
import java.util.List;

public class Playground {
    private String id;
    private String program;
    private ArrayList<String> history = new ArrayList<>(); // todo: this should be an array of date - eval result so it can be sorted in the client

    public Playground(final String id) {
        this.id = id;
    }

    public Playground(final String id, final String program, final List<String> history) {
        this.id = id;
        this.program = program;
        this.history = new ArrayList<>(history);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }

    public void addHistoryResult(String result) {
        this.history.add(result);
    }

    @Override
    public String toString() {
        return "playgroundId" + id + " history: " + history;
    }
}
