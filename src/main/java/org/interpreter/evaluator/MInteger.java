package org.interpreter.evaluator;

import org.interpreter.evaluator.object.HashKey;
import org.interpreter.evaluator.object.Hashable;

public class MInteger implements MObject, Hashable {

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

    @Override
    public HashKey getHashKey() {
        return HashKey.getHashKey(this);
    }
}
