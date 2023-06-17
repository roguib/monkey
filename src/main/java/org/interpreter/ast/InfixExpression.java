package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class InfixExpression implements Expression {

    private Token token;
    private Expression left;
    private String operator;
    private Expression right;

    InfixExpression(Token token, String operator, Expression left) {
        this.token = token;
        this.operator = operator;
        this.left = left;
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

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " " +  operator + " " + right + ")";
    }
}
