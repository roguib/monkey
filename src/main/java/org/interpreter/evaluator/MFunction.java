package org.interpreter.evaluator;

import org.interpreter.ast.BlockStatement;
import org.interpreter.ast.Identifier;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MFunction implements MObject {
    private ArrayList<Identifier> parameters = new ArrayList<>();
    private BlockStatement body;
    private Environment env;

    public MFunction(final ArrayList<Identifier> parameters, final BlockStatement body, final Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    @Override
    public MObjectType type() {
        return MObjectType.FUNCTION;
    }

    @Override
    public String inspect() {
        ArrayList<String> params = new ArrayList<>();
        for (final Identifier ident: parameters) {
            params.add(ident.toString());
        }
        StringBuilder sb = new StringBuilder();
        params.forEach(param -> sb.append(param));

        return "fn(" +
            params.stream().map(Object::toString)
            .collect(Collectors.joining(", ")) + ") {\n" +
            body.toString() +
            "\n}";
    }

    public ArrayList<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }
}
