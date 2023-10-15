package org.interpreter.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;

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
            assertEquals(result.toString(), token.toString());
        }
    }

    @Test
    public void testNextTokenSimpleProgram() {
        final String program = readProgram("src/test/resources/fixtures/program.monkey");
        final Lexer lexer = new Lexer(program);
        final Token[] results = {
                // let five = 5;
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "five"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),

                // let ten = 10;
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "ten"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.SEMICOLON, ";"),

                // let add = fn(x, y) {
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "add"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.FUNCTION, "fn"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.IDENT, "x"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENT, "y"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),

                // x + y;
                new Token(TokenType.IDENT, "x"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENT, "y"),
                new Token(TokenType.SEMICOLON, ";"),

                // }
                new Token(TokenType.RBRACE, "}"),

                // let result = add(five, ten);
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "result"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.IDENT, "add"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.IDENT, "five"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENT, "ten"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.SEMICOLON, ";"),

                // !-/*5;
                new Token(TokenType.BANG, "!"),
                new Token(TokenType.MINUS, "-"),
                new Token(TokenType.SLASH, "/"),
                new Token(TokenType.ASTERISK, "*"),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),

                // 5 < 10 > 5;
                new Token(TokenType.INT, "5"),
                new Token(TokenType.LT, "<"),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.GT, ">"),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),

                // if (5 < 10) {
                new Token(TokenType.IF, "if"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.LT, "<"),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),

                // return true;
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.TRUE, "true"),
                new Token(TokenType.SEMICOLON, ";"),

                // } else {
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.ELSE, "else"),
                new Token(TokenType.LBRACE, "{"),

                // return false;
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.FALSE, "false"),
                new Token(TokenType.SEMICOLON, ";"),

                // }
                new Token(TokenType.RBRACE, "}"),

                // 10 == 10;
                new Token(TokenType.INT, "10"),
                new Token(TokenType.EQ, "=="),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.SEMICOLON, ";"),

                // 10 != 9;
                new Token(TokenType.INT, "10"),
                new Token(TokenType.NOT_EQ, "!="),
                new Token(TokenType.INT, "9"),
                new Token(TokenType.SEMICOLON, ";"),

                // strings
                new Token(TokenType.STRING, "foobar"),
                new Token(TokenType.STRING, "foo bar"),

                // arrays
                new Token(TokenType.LBRACKET, "["),
                new Token(TokenType.INT, "1"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.INT, "2"),
                new Token(TokenType.RBRACKET, "]"),
                new Token(TokenType.SEMICOLON, ";"),

                // {"foo": "bar"}
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.STRING, "foo"),
                new Token(TokenType.COLON, ":"),
                new Token(TokenType.STRING, "bar"),
                new Token(TokenType.RBRACE, "}"),

                new Token(TokenType.EOF, ""),
        };
        for (final Token result : results) {
            final Token token = lexer.nextToken();
            assertEquals(result.toString(), token.toString());
        }
    }

    private String readProgram(final String path) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
                if (line != null) {
                    sb.append(System.getProperty("line.separator"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
