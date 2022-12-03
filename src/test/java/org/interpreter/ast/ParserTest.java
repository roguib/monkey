package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testLetStatements() {
        final String input = readProgram("src/test/resources/fixtures/parser.monkey");
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

    private void testLetStatement(final Statement stmt, final String expectedIdentifier) {
        assertTrue(stmt instanceof LetStatement);
        assertEquals("let", stmt.tokenLiteral());
        final LetStatement letStmt = (LetStatement) stmt;
        assertEquals(expectedIdentifier, letStmt.getName().getValue());
        assertEquals(expectedIdentifier, letStmt.getName().tokenLiteral());
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
