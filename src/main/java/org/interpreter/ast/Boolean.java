package org.interpreter.ast;

import org.interpreter.lexer.Token;

/**
 * A representation in the Monkey language of a boolean
 */
public class Boolean implements Expression {
    private Token token;
    private boolean value;

    public Boolean(Token token, boolean value) {
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

    public boolean getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
