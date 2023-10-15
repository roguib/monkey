package org.interpreter.ast;

import org.interpreter.lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HashLiteral implements Expression {
    private Token token;
    private HashMap<Expression, Expression> pairs;

    public HashLiteral(final Token token) {
        this.token = token;
    }

    public HashMap<Expression, Expression> getPairs() {
        return pairs;
    }

    public void setPairs(HashMap<Expression, Expression> pairs) {
        this.pairs = pairs;
    }

    @Override
    public void expressionNode() {
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        ArrayList<String> mapContent = new ArrayList<>();
        for(Map.Entry<Expression, Expression> entry : pairs.entrySet()) {
            String strEntry = entry.getKey().toString();
            mapContent.add(
                (entry.getKey() instanceof StringLiteral ? "\"" + strEntry + "\"" : strEntry) + ": " +
                entry.getValue().toString()
            );
        }
        return "{" + mapContent.stream().map(Object::toString)
            .collect(Collectors.joining(", ")) + "}";
    }
}
