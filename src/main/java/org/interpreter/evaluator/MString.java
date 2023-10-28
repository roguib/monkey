package org.interpreter.evaluator;

import org.interpreter.evaluator.object.HashKey;
import org.interpreter.evaluator.object.Hashable;

public class MString  implements MObject, Hashable {
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

    @Override
    public HashKey getHashKey() {
        return HashKey.getHashKey(this);
    }
}
