package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class StringLiteral implements Expression {
    private Token token;
    private String value;

    public StringLiteral(final Token token, final String value) {
        this.token = token;
        this.value = value;
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
        return token.getLiteral();
    }

    public String getValue() {
        return value;
    }
}
