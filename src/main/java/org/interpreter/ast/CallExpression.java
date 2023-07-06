package org.interpreter.ast;

import org.interpreter.lexer.Token;

import java.util.ArrayList;

public class CallExpression implements Expression {
    private Token token;
    private Expression function;
    private Expression[] arguments;

    CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    public Expression getFunction() {
        return function;
    }

    public void setFunction(final Expression function) {
        this.function = function;
    }

    public Expression[] getArguments() {
        return arguments;
    }

    public void setArguments(final Expression[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        ArrayList<String> args = new ArrayList<>();

        for (final Expression arg: arguments) {
            args.add(arg.toString());
        }

        StringBuilder sb = new StringBuilder();
        if (arguments.length > 0) {
            sb.append(arguments[0]);
        }

        for (int i = 1; i < arguments.length; ++i) {
            sb.append(", " + arguments[i]);
        }
        return function.toString() + "(" + sb + ")";
    }
}
