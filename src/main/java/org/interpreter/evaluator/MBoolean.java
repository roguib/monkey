package org.interpreter.evaluator;

public class MBoolean implements MObject {

    private boolean value;

    @Override
    public MObjectType type() {
        return MObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }
}
