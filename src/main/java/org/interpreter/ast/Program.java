package org.interpreter.ast;

import java.util.ArrayList;

/**
 * The root node of every AST that our parser produces. Every Monkey program is
 * a series of statements contained in this class.
 */
public class Program implements Node {
    private ArrayList<Statement> statements = new ArrayList<>();

    /**
     * Returns the literal value of the token it's associated with
     * @return {String} tokenLiteral
     */
    @Override
    public String tokenLiteral() {
        if (statements.size() > 0) {
            return statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    /**
     * Returns all the statements in a program
     * @return ArrayList<Statement> statements
     */
    public ArrayList<Statement> getStatements() {
        return statements;
    }

    /**
     * Adds one statement in the list of statements of a program
     * @param {Statement} stmt
     */
    public void addStatement(final Statement stmt) {
        statements.add(stmt);
    }
}
