package org.interpreter.lexer;

public class Lexer {
    private final String input;
    private int position;
    private int readPosition;
    private char ch;

    public Lexer(final String input) {
        this.input = input;
    }

    public Token nextToken() {
        return new Token(TokenType.ILLEGAL, "");
    }
}
