package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class Identifier implements Expression {
    private Token token;
    private String value;

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }
}
