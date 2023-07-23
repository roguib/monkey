package org.interpreter.evaluator;

public interface MObject {
    MObjectType type();
    String inspect();
}
