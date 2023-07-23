package org.interpreter.evaluator;

public class Boolean implements Object {

    private boolean value;

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }
}
