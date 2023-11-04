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

    // This is needed because we're using a custom class HashKey as a key for the internal
    // pairs HashMap. Otherwise, the pairs hash map wouldn't be able to distinguish between
    // the same two Hash Keys objects, always returning false when calling pairs.hasKey(<HashKey>)
    // see: https://www.baeldung.com/java-custom-class-map-key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final HashKey that = (HashKey) o;
        return this.type == that.type && this.value == that.value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }
}
