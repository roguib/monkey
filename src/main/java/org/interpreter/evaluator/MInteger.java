package org.interpreter.evaluator;

public class MInteger implements MObject {

    private int value;

    public MInteger(final int value) {
        this.value = value;
    }

    @Override
    public MObjectType type() {
        return MObjectType.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }
}
