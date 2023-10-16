package org.interpreter.object;

import org.interpreter.evaluator.MString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ObjectTest {

    @Test
    public void testStringHashKey() {
        final MString hello1 = new MString("Hello world");
        final MString hello2 = new MString("Hello world");
        final MString diff1 = new MString("My name is Johnny");
        final MString diff2 = new MString("My name is Johnny");

        assertEquals(hello1.getHashKey().getValue(), hello2.getHashKey().getValue());
        assertEquals(diff1.getHashKey().getValue(), diff2.getHashKey().getValue());
        assertNotEquals(hello1.getHashKey().getValue(), diff1.getHashKey().getValue());
    }
}
