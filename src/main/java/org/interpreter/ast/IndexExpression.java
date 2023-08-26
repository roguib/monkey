package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class IndexExpression implements Expression {
    private Token token;
    private Expression left;
    private Expression index;

    public IndexExpression(final Token token, final Expression left) {
        this.token = token;
        this.left = left;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getIndex() {
        return index;
    }

    public void setIndex(final Expression index) {
        this.index = index;
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
        return "(" + left.toString() + "[" + index.toString() + "])";
    }
}
