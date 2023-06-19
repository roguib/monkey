package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        final ExpressionStatement exprstmt = (ExpressionStatement) program.getStatements().get(0);
        final Identifier identifier = (Identifier) exprstmt.getExpression();
        assertEquals(identifier.getValue(), "foovar");
        assertEquals(identifier.tokenLiteral(), "foovar");
    }

    @Test
    public void testBooleanFalseLiteralExpression() {
        final String input = "false;";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final Statement s = program.getStatements().get(0);
        final Boolean bool = (Boolean) ((ExpressionStatement) s).getExpression();
        assertTrue(bool != null);
        assertEquals(bool.getValue(), false);
    }

    @Test
    public void testBooleanTrueLiteralExpression() {
        final String input = "true;";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final Statement s = program.getStatements().get(0);
        final Boolean bool = (Boolean) ((ExpressionStatement) s).getExpression();
        assertTrue(bool != null);
        assertEquals(bool.getValue(), true);
    }

    @Test
    public void testIntegerLiteralExpression() {
        final String input = "5;";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final Statement s = program.getStatements().get(0);
        assertTrue(s instanceof ExpressionStatement);
        final IntegerLiteral literal = (IntegerLiteral) ((ExpressionStatement) s).getExpression();
        assertTrue(literal instanceof IntegerLiteral);
        assertEquals(literal.getValue(), 5);
    }

    private void testIntegerLiteral(Expression exp, int value) {
        IntegerLiteral integ = (IntegerLiteral) exp;
        assertEquals(integ.getValue(), value);
        assertEquals(integ.getToken().getLiteral(), String.valueOf(value));
    }

    private void testIdentifier(Expression exp, String value) {
        final Identifier ident = (Identifier) exp;
        assertEquals(ident.getValue(), value);
        assertEquals(ident.tokenLiteral(), value);
    }

    private void testBooleanLiteral(Expression exp, java.lang.Boolean value) {
        final Boolean bool = (Boolean) exp;
        assertEquals(bool.getValue(), value);
        assertEquals(bool.tokenLiteral(), String.valueOf(value));
    }

    private void testLiteralExpression(Expression exp, Object expected) {
        if (expected instanceof Integer) {
            testIntegerLiteral(exp, (Integer) expected);
        } else if (expected instanceof String) {
            testIdentifier(exp, (String) expected);
        } else if (expected instanceof java.lang.Boolean) {
            testBooleanLiteral(exp, (java.lang.Boolean) expected);
        }
    }

    private void testInfixExpression(Expression exp, Object left, String operator, Object right) {
        final InfixExpression opExp = (InfixExpression) exp;
        testLiteralExpression(opExp.getLeft(), left);
        assertEquals(opExp.getOperator(), operator);
        testLiteralExpression(opExp.getRight(), right);
    }

    @Test
    public void testParsePrefixExpressions() {
        Lexer l = new Lexer("!5");
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        ExpressionStatement es = (ExpressionStatement) program.getStatements().get(0);
        PrefixExpression exp = (PrefixExpression) es.getExpression();
        assertEquals(exp.getOperator(), "!");
        testIntegerLiteral(exp.getRight(), 5);

        l = new Lexer("-15");
        p = new Parser(l);
        program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        es = (ExpressionStatement) program.getStatements().get(0);
        exp = (PrefixExpression) es.getExpression();
        assertEquals(exp.getOperator(), "-");
        testIntegerLiteral(exp.getRight(), 15);

        l = new Lexer("!true");
        p = new Parser(l);
        program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        es = (ExpressionStatement) program.getStatements().get(0);
        exp = (PrefixExpression) es.getExpression();
        assertEquals(exp.getOperator(), "!");
        testBooleanLiteral(exp.getRight(), true);
    }

    @Test
    public void testParseInfixExpressions() {
        final String[] input = {
                "5 + 5;",
                "5 - 5",
                "5 * 5",
                "5 / 5",
                "5 > 5",
                "5 < 5",
                "5 == 5",
                "5 != 5",
                "true == true",
                "true != false",
                "false == false",
        };
        final Object[] leftValue = {
                5,
                5,
                5,
                5,
                5,
                5,
                5,
                5,
                true,
                true,
                false
        };
        final String[] operator = {
                "+",
                "-",
                "*",
                "/",
                ">",
                "<",
                "==",
                "!=",
                "==",
                "!=",
                "=="
        };
        final Object[] rightValue = {
                5,
                5,
                5,
                5,
                5,
                5,
                5,
                5,
                true,
                false,
                false
        };

        for(int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);

            // contains exactly one statement
            final ArrayList<Statement> statements = program.getStatements();
            assertEquals(statements.size(), 1);
            ExpressionStatement stmt = (ExpressionStatement) statements.get(0);
            InfixExpression exp = (InfixExpression) stmt.getExpression();
            testLiteralExpression(exp.getLeft(), leftValue[i]);
            assertEquals(exp.getOperator(), operator[i]);
            testLiteralExpression(exp.getRight(), rightValue[i]);
        }
    }

    @Test
    public void testOperatorPrecedence() {
        final String[] input = {
                "-a * b",
                "!-a",
                "a + b + c",
                "a + b - c",
                "a * b * c",
                "a * b / c",
                "a + b / c",
                "a + b * c + d / e - f",
                "3 + 4; -5 * 5",
                "5 > 4 == 3 < 4",
                "5 < 4 != 3 > 4",
                "3 + 4 * 5 == 3 * 1 + 4 * 5",
                "3 + 4 * 5 == 3 * 1 + 4 * 5",
                "true",
                "false",
                "3 > 5 == false",
                "3 < 5 == true",
                "1 + (2 + 3) + 4",
                "(5 + 5) * 2",
                "2 / (5 + 5)",
                "-(5 + 5)",
        };
        final String[] expected = {
                "((-a) * b)",
                "(!(-a))",
                "((a + b) + c)",
                "((a + b) - c)",
                "((a * b) * c)",
                "((a * b) / c)",
                "(a + (b / c))",
                "(((a + (b * c)) + (d / e)) - f)",
                "(3 + 4)((-5) * 5)",
                "((5 > 4) == (3 < 4))",
                "((5 < 4) != (3 > 4))",
                "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",
                "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",
                "true",
                "false",
                "((3 > 5) == false)",
                "((3 < 5) == true)",
                "((1 + (2 + 3)) + 4)",
                "((5 + 5) * 2)",
                "(2 / (5 + 5))",
                "(-(5 + 5))",
        };
        for (int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);
            assertEquals(program.toString(), expected[i]);
        }
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
        final List<String> errors = p.getErrors();
        errors.forEach(err -> System.out.println(err));
        assertEquals(errors.size(), 0);
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
