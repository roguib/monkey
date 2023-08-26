package org.interpreter.evaluator;

import org.interpreter.ast.Identifier;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MArray implements MObject {
    private MObject[] elements;

    public MArray(final MObject[] elements) {
        this.elements = elements;
    }

    public MObject[] getElements() {
        return this.elements;
    }

    @Override
    public MObjectType type() {
        return MObjectType.ARRAY;
    }

    @Override
    public String inspect() {
        ArrayList<String> elems = new ArrayList<>();
        for (final MObject e: elements) {
            elems.add(e.inspect());
        }
        StringBuilder sb = new StringBuilder();
        elems.forEach(param -> sb.append(param));

        return "[" +
                elems.stream().map(Object::toString)
                        .collect(Collectors.joining(", ")) +
                "]";
    }
}
