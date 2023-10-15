package org.interpreter.evaluator.object;

import org.interpreter.evaluator.MBoolean;
import org.interpreter.evaluator.MInteger;
import org.interpreter.evaluator.MObjectType;
import org.interpreter.evaluator.MString;

public class HashKey {
    private MObjectType type;
    private int value;

    public HashKey(final MObjectType type, final int value) {
        this.type = type;
        this.value = value;
    }

    public static HashKey getHashKey(final MBoolean bool) {
        int value = bool.getValue() ? 1 : 0;
        return new HashKey(bool.type(), value);
    }

    public static HashKey getHashKey(final MInteger integer) {
        return new HashKey(integer.type(), integer.getValue());
    }

    public static HashKey getHashKey(final MString string) {
        int value = string.getValue().hashCode();
        return new HashKey(string.type(), value);
    }

    public int getValue() {
        return value;
    }
}
