package org.interpreter.evaluator;

public class Null implements Object {

    final private String value = "null";

    @Override
    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String inspect() {
        return value;
    }
}
