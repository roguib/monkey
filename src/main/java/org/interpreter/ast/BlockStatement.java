package org.interpreter.ast;

import org.interpreter.lexer.Token;

public class BlockStatement implements Statement {
    private Token token;
    private Statement[] statements;

    BlockStatement(Token token) {
        this.token = token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }

    public Statement[] getStatements() {
        return statements;
    }

    public void setStatements(final Statement[] statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        for (final Statement s: statements) {
            out.append(s.toString());
        }
        return out.toString();
    }
}
