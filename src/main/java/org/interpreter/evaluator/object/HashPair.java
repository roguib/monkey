package org.interpreter.evaluator.object;

import org.interpreter.evaluator.MObject;

public class HashPair {
    public MObject key;
    public MObject value;

    public HashPair(MObject key, MObject value) {
        this.key = key;
        this.value = value;
    }
}