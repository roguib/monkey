package org.interpreter.lexer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LexerTest {
    private final Lexer lexer = new Lexer("=+(){},;");

    @Test
    public void testNextToken() {
        final Token[] results = {
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.EOF, ""),
        };
        for (final Token result : results) {
            final boolean isEqual = result.equals(lexer.nextToken());
            assertTrue(isEqual);
        }
    }
}
