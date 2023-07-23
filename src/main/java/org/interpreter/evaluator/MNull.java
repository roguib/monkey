package org.interpreter.evaluator;

public class MNull implements MObject {

    final private String value = "null";

    @Override
    public MObjectType type() {
        return MObjectType.NULL;
    }

    @Override
    public String inspect() {
        return value;
    }
}
