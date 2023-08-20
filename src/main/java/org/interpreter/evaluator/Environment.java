package org.interpreter.evaluator;

import java.util.HashMap;

// For each variable we keep track of its value by using the Environment class
public class Environment {
    private final HashMap<String, MObject> store = new HashMap<>();
    private Environment outer;

    public MObject get(final String name) {
        if (store.containsKey(name)) {
            return store.get(name);
        } else if (outer != null) {
            return outer.get(name);
        }
        return new MNull();
    }

    public void set(final String name, final MObject value) {
        store.put(name, value);
    }

    public static Environment newEnclosedEnvironment(Environment outer) {
        Environment env = new Environment();
        env.setOuterEnvironment(outer);
        return env;
    }

    public void setOuterEnvironment(Environment outer) {
        this.outer = outer;
    }

}
