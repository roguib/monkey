package org.interpreter.ast;

import org.interpreter.lexer.Token;

import java.util.ArrayList;

/**
 * Function representation: fn <parameters> <block statement> where <parameters> can be (<p1>, <p2>, ..., <pn>) or
 * an empty list.
 * Functions can be assigned to let statement or a function can be part of a parameter passed to another function
 */
public class FunctionLiteral implements Expression {
    private Token token;
    private Identifier[] parameters;
    private BlockStatement body;

    FunctionLiteral(Token token) {
        this.token = token;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return null;
    }

    public Identifier[] getParameters() {
        return parameters;
    }

    public void setParameters(final Identifier[] parameters) {
        this.parameters = parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        ArrayList<String> params = new ArrayList<>();
        for (final Identifier ident: parameters) {
            params.add(ident.toString());
        }
        return token.getLiteral() + "(" + params.toString() + ") " + body.toString();
    }
}
