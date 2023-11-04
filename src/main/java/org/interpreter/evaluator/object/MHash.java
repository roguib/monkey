package org.interpreter.evaluator.object;

import org.interpreter.ast.Expression;
import org.interpreter.ast.IntegerLiteral;
import org.interpreter.ast.StringLiteral;
import org.interpreter.evaluator.MObject;
import org.interpreter.evaluator.MObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MHash implements MObject {
    private HashMap<HashKey, HashPair> pairs;

    public MHash(final HashMap<HashKey, HashPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public MObjectType type() {
        return MObjectType.HASH;
    }

    public HashMap<HashKey, HashPair> getPairs() {
        return this.pairs;
    }

    @Override
    public String inspect() {
        ArrayList<String> pairs = new ArrayList<>();
        for (Map.Entry<HashKey, HashPair> entry : this.pairs.entrySet()) {
            final HashPair mapValue =  entry.getValue();
            pairs.add(mapValue.key.inspect() + ": " + mapValue.value.inspect());
        }
        return "{" +
            pairs.stream().map(Object::toString)
            .collect(Collectors.joining(", ")) +
            "}";
    }
}
