package org.interpreter.lexer;

import java.util.HashMap;

public class Lexer {
    private final String input;
    // points to the character in the input that corresponds to the ch char
    private int position;
    // points to the next character of ch
    private int readPosition;
    // current character being read
    private char ch;
    // a mapping between keywords and token types
    private final HashMap<String, TokenType> keywords = new HashMap<>();
    {
        keywords.put("fn", TokenType.FUNCTION);
        keywords.put("let", TokenType.LET);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("return", TokenType.RETURN);
    }

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

    private String readString() {
        int position = this.position + 1;
        readChar();
        while (ch != '\"' && ch != 0) {
            readChar();
        }
        return input.substring(position, this.position);
    }

    /**
     * Returns the next character pointed by readPosition without incrementing
     * position nor readPosition indexes
     * @return The next character or 0 if readPosition >= input.length()
     */
    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        } else {
            return input.charAt(readPosition);
        }
    }

    /**
     *
     * @param c
     * @return True if character c is a letter, false otherwise
     */
    private boolean isLetter(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_';
    }

    /**
     *
     * @param c
     * @return True if character c is a number, false otherwise
     */
    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * Reads an identifier from the input and returns it
     * @return The identifier, that is: the set of consecutive characters
     * that are letters
     */
    private String readIdentifier() {
        final int beginPos = position;
        while(isLetter(ch)) {
            readChar();
        }
        return input.substring(beginPos, position);
    }

    /**
     * Queries keywords hashmap to return the appropriate TokenType of the
     * provided identifier
     * @param identifier
     * @return The TokenType that matches the identifier or ILLEGAL if it
     * doesn't match any
     */
    private TokenType lookupIdentifier(final String identifier) {
        TokenType type = keywords.get(identifier);
        return type != null ? type : TokenType.IDENT;
    }

    /**
     * Reads a number from the input and returns it
     * @return The set of consecutive characters that are digits
     */
    private String readNumber() {
        final int beginPos = position;
        while(isDigit(ch)) {
            readChar();
        }
        return input.substring(beginPos, position);
    }

    /**
     * Reads as many chars as needed to skip all consecutive whitespaces
     */
    private void skipWhitespace() {
        while(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
    }

    public Token nextToken() {
        Token token;

        skipWhitespace();

        switch(ch) {
            case '=':
                if (peekChar() == '=') {
                    final String firstCh = String.valueOf(ch);
                    readChar();
                    token = new Token(TokenType.EQ, firstCh + ch);
                } else {
                    token = new Token(TokenType.ASSIGN, ch);
                }
                break;
            case ';':
                token = new Token(TokenType.SEMICOLON, ch);
                break;
            case '(':
                token = new Token(TokenType.LPAREN, ch);
                break;
            case ')':
                token = new Token(TokenType.RPAREN, ch);
                break;
            case '{':
                token = new Token(TokenType.LBRACE, ch);
                break;
            case '}':
                token = new Token(TokenType.RBRACE, ch);
                break;
            case ',':
                token = new Token(TokenType.COMMA, ch);
                break;
            case '+':
                token = new Token(TokenType.PLUS, ch);
                break;
            case '-':
                token = new Token(TokenType.MINUS, ch);
                break;
            case '*':
                token = new Token(TokenType.ASTERISK, ch);
                break;
            case '/':
                token = new Token(TokenType.SLASH, ch);
                break;
            case '<':
                token = new Token(TokenType.LT, ch);
                break;
            case '>':
                token = new Token(TokenType.GT, ch);
                break;
            case '!':
                if (peekChar() == '=') {
                    final String firstCh = String.valueOf(ch);
                    readChar();
                    token = new Token(TokenType.NOT_EQ, firstCh + ch);
                } else {
                    token = new Token(TokenType.BANG, ch);
                }
                break;
            case '"':
                token = new Token(TokenType.STRING, readString());
                break;
            default:
                if (isLetter(ch)) {
                    final String literal = readIdentifier();
                    final TokenType type = lookupIdentifier(literal);
                    token = new Token(type, literal);
                    return token;
                } else if (isDigit(ch)) {
                    final String literal = readNumber();
                    token = new Token(TokenType.INT, literal);
                    return token;
                }
                else {
                    token = new Token(TokenType.EOF, "");
                }
                break;
        }

        readChar();
        return token;
    }
}
