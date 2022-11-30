package org.interpreter.ast;

/**
 * The root node of every AST that our parser produces. Every Monkey program is
 * a series of statements contained in this class.
 */
public class Program {
    private Statement[] statements;

    public String tokenLiteral() {
        if (statements.length > 0) {
            return statements[0].tokenLiteral();
        } else {
            return "";
        }
    }
}
