package org.interpreter.evaluator;

public class MError implements MObject {

    private String message;

    public MError(final String message) {
        this.message = message;
    }

    @Override
    public MObjectType type() {
        return MObjectType.ERROR;
    }

    @Override
    public String inspect() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
