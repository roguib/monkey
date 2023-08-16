package org.interpreter.evaluator;

import java.util.HashMap;

// For each variable we keep track of its value by using the Environment class
public class Environment {
    private final HashMap<String, MObject> store = new HashMap<>();

    public MObject get(final String name) {
        if (store.containsKey(name)) {
            return store.get(name);
        }
        return new MNull();
    }

    public void set(final String name, final MObject value) {
        store.put(name, value);
    }

}
