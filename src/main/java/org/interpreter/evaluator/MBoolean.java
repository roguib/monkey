package org.interpreter.evaluator;

import org.interpreter.evaluator.object.HashKey;
import org.interpreter.evaluator.object.Hashable;

public class MBoolean implements MObject, Hashable {

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

    @Override
    public HashKey getHashKey() {
        return HashKey.getHashKey(this);
    }
}
