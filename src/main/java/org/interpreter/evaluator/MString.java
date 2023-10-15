package org.interpreter.evaluator;

import org.interpreter.evaluator.object.HashKey;

public class MString  implements  MObject {
    private String value;

    public MString(final String value) {
        this.value = value;
    }

    @Override
    public MObjectType type() {
        return MObjectType.STRING;
    }

    @Override
    public String inspect() {
        return value;
    }

    public String getValue() {
        return this.value;
    }

    public HashKey getHashKey() {
        return HashKey.getHashKey(this);
    }
}
