package org.interpreter.evaluator;

public class Builtin implements BuiltinInterface, MObject {

    private BuiltinInterface fn;

    public Builtin(BuiltinInterface fn) {
        this.fn = fn;
    }

    @Override
    public MObjectType type() {
        return MObjectType.BUILTIN;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }

    @Override
    public MObject fn(MObject... args) {
        return fn.fn(args);
    }
}
