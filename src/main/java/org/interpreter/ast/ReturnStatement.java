package org.interpreter.ast;

import org.interpreter.lexer.Token;

/**
 * A representation of a return statement
 * A return statement is defined in the Monkey language as:
 * return <expression>;
 */
public class ReturnStatement implements Statement {
    private Token token;
    private Expression returnValue;

    public ReturnStatement(Token token) {
        this.token = token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }
}
