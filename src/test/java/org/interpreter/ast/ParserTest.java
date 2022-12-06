package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testLetStatements() {
        final String input = readProgram("src/test/resources/fixtures/let-statements.monkey");
        // no stub to simplify the test
        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);

        final Program program = p.parseProgram();
        assertNotNull(program);
        assertEquals(program.getStatements().size(), 3);

        final String[] tests = { "x", "y", "foobar" };
        for (int i = 0; i < tests.length; ++i) {
            final Statement stmt = program.getStatements().get(i);
            testLetStatement(stmt, tests[i]);
        }
    }

    @Test
    public void testReturnStatements() {
        final String input = readProgram("src/test/resources/fixtures/return-statements.monkey");
        // no stub to simplify the test
        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);

        final Program program = p.parseProgram();
        assertNotNull(program);
        assertEquals(program.getStatements().size(), 3);

        for (int i = 0; i < program.getStatements().size(); ++i) {
            final Statement stmt = program.getStatements().get(i);
            testReturnStatement(stmt, "return");
        }
    }

    @Test
    public void testToString() {
        final String input = readProgram("src/test/resources/fixtures/toString-parser.monkey");

        final Program p = new Program();
        p.addStatement(
            new LetStatement(
                new Token(TokenType.LET, "let"),
                new Identifier(
                    new Token(TokenType.IDENT, "myVar"),
                    "myVar"
                ),
                new Identifier(
                    new Token(TokenType.IDENT, "anotherVar"),
                    "anotherVar"
                )
            )
        );

        assertEquals(p.toString(), input);
    }

    @Test
    public void testIdentifierExpression() {
        final String input = "foovar;";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        assertTrue(program.getStatements().get(0) instanceof Identifier);
        final Identifier identifier = (Identifier) program.getStatements().get(0);
        assertEquals(identifier.getValue(), "foobar");
        assertEquals(identifier.tokenLiteral(), "foobar");
    }

    private void testLetStatement(final Statement stmt, final String expectedIdentifier) {
        assertTrue(stmt instanceof LetStatement);
        assertEquals("let", stmt.tokenLiteral());
        final LetStatement letStmt = (LetStatement) stmt;
        assertEquals(expectedIdentifier, letStmt.getName().getValue());
        assertEquals(expectedIdentifier, letStmt.getName().tokenLiteral());
    }

    private void testReturnStatement(final Statement stmt, final String expectedTokenLiteral) {
        assertTrue(stmt instanceof ReturnStatement);
        assertEquals("return", stmt.tokenLiteral());
        final ReturnStatement returnStmt = (ReturnStatement) stmt;
        assertEquals(expectedTokenLiteral, returnStmt.tokenLiteral());
    }

    private void checkParserErrors(final Parser p) {
        assertEquals(p.getErrors(), 0);
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
