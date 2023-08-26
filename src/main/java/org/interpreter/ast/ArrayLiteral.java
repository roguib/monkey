package org.interpreter.ast;

import org.interpreter.lexer.Token;

import java.util.ArrayList;

public class ArrayLiteral implements Expression {
    private Token token;
    private Expression[] elements;

    public ArrayLiteral(final Token token, final Expression[] elements) {
        this.token = token;
        this.elements = elements;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    public Expression[] getElements() {
        return elements;
    }

    @Override
    public String toString() {
        ArrayList<String> elems = new ArrayList<>();

        for (final Expression arg: elements) {
            elems.add(arg.toString());
        }

        StringBuilder sb = new StringBuilder();
        if (elements.length > 0) {
            sb.append(elements[0]);
        }

        for (int i = 1; i < elements.length; ++i) {
            sb.append(", " + elements[i]);
        }

        return "[" + sb + "]";
    }
}
