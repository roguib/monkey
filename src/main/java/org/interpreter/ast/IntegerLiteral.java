package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class IntegerLiteral implements Expression {

    private Token token;
    private int value;

    public IntegerLiteral(Token token) {
        this.token = token;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return tokenLiteral();
    }
}
