package org.playground.ws;

import java.util.ArrayList;

public class Playground {
    public Playground(final String id) {
        this.id = id;
    }
    private String id;
    private ArrayList<String> history = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }
}
