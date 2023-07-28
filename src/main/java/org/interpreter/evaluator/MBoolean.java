package org.interpreter.evaluator;

public class MBoolean implements MObject {

    private final boolean value;

    public MBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public MObjectType type() {
        return MObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    public boolean getValue() {
        return value;
    }
}
