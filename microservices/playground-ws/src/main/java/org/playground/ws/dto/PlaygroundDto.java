package org.playground.ws.dto;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundDto {
    private String id;
    private String program;
    private ArrayList<PlaygroundHistoryDto> history = new ArrayList<>();

    public PlaygroundDto(final String id) {
        this.id = id;
        this.program = "";
    }

    public PlaygroundDto(final String id, final String program, final List<PlaygroundHistoryDto> history) {
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

    public ArrayList<PlaygroundHistoryDto> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<PlaygroundHistoryDto> history) {
        this.history = history;
    }

    public void addHistoryResult(final PlaygroundHistoryDto result) {
        this.history.add(result);
    }

    @Override
    public String toString() {
        return "playgroundId" + id + " history: " + history + " program: " + program;
    }
}
