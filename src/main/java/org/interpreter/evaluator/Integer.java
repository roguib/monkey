package org.interpreter.evaluator;

public class Integer implements Object {

    private int value;

    public Integer(final int value) {
        this.value = value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }
}
