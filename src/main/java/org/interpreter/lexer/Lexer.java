package org.interpreter.lexer;

public class Lexer {
    private final String input;
    // points to the character in the input that corresponds to the ch char
    private int position;
    // points to the next character of ch
    private int readPosition;
    // current character being read
    private char ch;

    public Lexer(final String input) {
        this.input = input;
        readChar();
    }

    /**
     * Sets ch variable to the next character in the input, or 0 if it has
     * reached the end. It also updates position and readPosition class variables.
     * Note it doesn't support UTF-8, it just supports ASCII
     */
    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0;
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        readPosition += 1;
    }

    public Token nextToken() {
        Token token;
        switch(ch) {
            case '=':
                token = new Token(TokenType.ASSIGN, String.valueOf(ch));
                break;
            case ';':
                token = new Token(TokenType.SEMICOLON, String.valueOf(ch));
                break;
            case '(':
                token = new Token(TokenType.LPAREN, String.valueOf(ch));
                break;
            case ')':
                token = new Token(TokenType.RPAREN, String.valueOf(ch));
                break;
            case '{':
                token = new Token(TokenType.LBRACE, String.valueOf(ch));
                break;
            case '}':
                token = new Token(TokenType.RBRACE, String.valueOf(ch));
                break;
            case ',':
                token = new Token(TokenType.COMMA, String.valueOf(ch));
                break;
            case '+':
                token = new Token(TokenType.PLUS, String.valueOf(ch));
                break;
            default:
                token = new Token(TokenType.EOF, "");
                break;
        }

        readChar();
        return token != null ? token : new Token(TokenType.ILLEGAL, "");
    }
}
