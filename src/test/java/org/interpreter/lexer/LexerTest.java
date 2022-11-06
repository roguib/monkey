package org.interpreter.lexer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LexerTest {
    @Test
    public void testNextTokenBasic() {
        final Lexer lexer = new Lexer("=+(){},;");
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
            final Token token = lexer.nextToken();
            final boolean isEqual = result.equals(token);
            assertTrue(isEqual);
        }
    }
}
