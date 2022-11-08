package org.interpreter.lexer;

public class Token {
    private TokenType type;
    private final String literal;

    private Token(final String literal) {
        this.literal = literal;
    }

    public Token(final TokenType type, final String literal) {
        this(literal);
        this.type = type;
    }

    public Token(final TokenType type, final char ch) {
        this(String.valueOf(ch));
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return "Token type: " + type + " literal value: " + literal;
    }

    public boolean equals(Token token) {
        return token.getLiteral().equals(this.literal) && token.getType() == this.type;
    }
}
