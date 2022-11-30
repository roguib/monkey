package org.interpreter.ast;

public interface Node {
    /**
     * Method that returns the literal value of the
     * token it's associated with
     * @return the literal value of the associated token
     */
    public String tokenLiteral();
}
