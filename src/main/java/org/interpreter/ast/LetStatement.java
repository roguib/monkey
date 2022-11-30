package org.interpreter.ast;

import org.interpreter.lexer.Token;

/**
 * A representation of a let statement.
 * A let statement is defined in the Monkey language as:
 * let <identifier> = <expression>
 *
 * @note
 * The identifier in a let statement (e.g let x = 5)
 * doesn't produce value, but for simplicity we use
 * an expression to represent it
 */
public class LetStatement implements Statement {
    private Token token;
    private Identifier identifier;
    private Expression value;

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }
}
