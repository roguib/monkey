package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class ExpressionStatement implements Statement {
    private Token token;
    private Expression expression;


    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String toString() {
        if (expression != null) {
            return expression.toString();
        }
        return "";
    }

    public Token getToken() {
        return token;
    }

    public Expression getExpression() {
        return expression;
    }
}
