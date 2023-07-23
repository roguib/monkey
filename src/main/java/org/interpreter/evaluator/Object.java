package org.interpreter.evaluator;

public interface Object {
    ObjectType type();
    String inspect();
}
