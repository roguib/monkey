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
    private Identifier name;
    private Expression value;

    public LetStatement(Token token) {
        this.token = token;
    }

    public LetStatement(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(token.getLiteral() + " " + name.toString() + " = ");

        if (value != null) {
            sb.append(value.toString());
        }

        sb.append(";");

        return sb.toString();
    }
}
