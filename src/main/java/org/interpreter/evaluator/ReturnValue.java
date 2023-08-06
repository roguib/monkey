package org.interpreter.evaluator;

public class ReturnValue implements MObject {

    private final MObject value;

    public ReturnValue(final MObject value) {
        this.value = value;
    }

    @Override
    public MObjectType type() {
        return MObjectType.RETURN_VALUE;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }

    public MObject getValue() {
        return value;
    }
}
