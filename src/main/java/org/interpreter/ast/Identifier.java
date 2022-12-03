package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class Identifier implements Expression {
    private Token token;
    private String value;

    public Identifier(Token token, String value) {
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

    public String getValue() {
        return this.value;
    }
}
