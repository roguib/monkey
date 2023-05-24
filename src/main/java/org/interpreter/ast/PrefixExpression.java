package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class PrefixExpression implements Expression {

    private Token token;
    private String operator;
    private Expression right;

    PrefixExpression(Token token, String operator) {
        this.token = token;
        this.operator = operator;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + operator + right + ")";
    }
}
