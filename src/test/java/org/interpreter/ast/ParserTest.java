package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testLetStatements() {
        final String[] input = {
                "let x = 5;",
                "let y = true;",
                "let foobar = y;",
        };
        final String[] expectedIdentifier = {
                "x",
                "y",
                "foobar",
        };
        final Object[] expectedValue = {
                5,
                true,
                "y",
        };

        for (int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);
            ArrayList<Statement> stmts = program.getStatements();
            assertEquals(stmts.size(), 1);
            final Statement stmt = stmts.get(0);

            testLetStatement(stmt, expectedIdentifier[i]);

            LetStatement letStmt = (LetStatement) stmt;
            testLiteralExpression(letStmt.getValue(), expectedValue[i]);
        }
    }

    @Test
    public void testReturnStatements() {
        final String[] input = {
                "return 5;",
                "return 10;",
                "return 993322;",
        };
        final Object[] expectedValue = {
                5,
                10,
                993322
        };

        for (int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);
            ArrayList<Statement> stmts = program.getStatements();
            assertEquals(stmts.size(), 1);
            final Statement stmt = stmts.get(0);

            testReturnStatement(stmt, "return");

            ReturnStatement returnStmt = (ReturnStatement) stmt;
            testLiteralExpression(returnStmt.getReturnValue(), expectedValue[i]);
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
        assertNotNull(bool);
        assertFalse(bool.getValue());
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
        assertNotNull(bool);
        assertTrue(bool.getValue());
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
        assertTrue(literal != null);
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
                "a + add(b * c) + d",
                "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                "add(a + b + c * d / f + g)",
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
                "((a + add((b * c))) + d)",
                "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))",
                "add((((a + b) + ((c * d) / f)) + g))",
        };
        for (int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);
            assertEquals(program.toString(), expected[i]);
        }
    }

    @Test
    public void testIfExpression() {
        final String input = "if (x < y) { x }";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        final IfExpression exp = (IfExpression) stmt.getExpression();
        testInfixExpression(exp.getCondition(), "x", "<", "y");
        assertEquals(exp.getConsequence().getStatements().length, 1);
        ExpressionStatement consequence = (ExpressionStatement) exp.getConsequence().getStatements()[0];
        testIdentifier(consequence.getExpression(), "x");
        assertNull(exp.getAlternative());
    }

    @Test
    public void testIfElseExpression() {
        final String input = "if (x < y) { x } else { y }";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        final IfExpression exp = (IfExpression) stmt.getExpression();
        testInfixExpression(exp.getCondition(), "x", "<", "y");
        assertEquals(exp.getConsequence().getStatements().length, 1);
        ExpressionStatement consequence = (ExpressionStatement) exp.getConsequence().getStatements()[0];
        testIdentifier(consequence.getExpression(), "x");
        assertNotNull(exp.getAlternative());
        ExpressionStatement alternative = (ExpressionStatement) exp.getAlternative().getStatements()[0];
        testIdentifier(alternative.getExpression(), "y");
    }

    @Test
    public void testFunctionLiteral() {
        final String input = "fn(x, y) { x + y }";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        final ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        final FunctionLiteral function = (FunctionLiteral) stmt.getExpression();

        final Identifier[] params = function.getParameters();
        assertEquals(params.length, 2);
        testLiteralExpression(params[0], "x");
        testLiteralExpression(params[1], "y");

        final Statement[] bodyStatements = function.getBody().getStatements();
        assertEquals(bodyStatements.length, 1);
        final ExpressionStatement bodyStmt = (ExpressionStatement) bodyStatements[0];
        testInfixExpression(bodyStmt.getExpression(), "x", "+", "y");
    }

    @Test
    public void testFunctionParameters() {
        final String[] input = {
                "fn () {};",
                "fn(x) {};",
                "fn(x, y, z) {};"
        };
        final String[][] expectedParams = {
                new String[0],
                new String[]{ "x" },
                new String[]{ "x", "y", "z" },
        };

        for (int i = 0; i < input.length; ++i) {
            final Lexer l = new Lexer(input[i]);
            final Parser p = new Parser(l);
            final Program program = p.parseProgram();
            checkParserErrors(p);

            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
            FunctionLiteral function = (FunctionLiteral) stmt.getExpression();
            assertEquals(function.getParameters().length, expectedParams[i].length);

            for(int ii = 0; ii < expectedParams[i].length; ++ii) {
                testLiteralExpression(function.getParameters()[ii], expectedParams[ii]);
            }
        }
    }

    @Test
    public void testCallExpression() {
        final String input = "add(1, 2 * 3, 4 + 5);";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(program.getStatements().size(), 1);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        CallExpression exp = (CallExpression) stmt.getExpression();
        testIdentifier(exp.getFunction(), "add");

        final Expression[] fnArg = exp.getArguments();
        assertEquals(fnArg.length, 3);

        testLiteralExpression(fnArg[0], 1);
        testInfixExpression(fnArg[1], 2, "*", 3);
        testInfixExpression(fnArg[2], 4, "+", 5);
    }

    @Test
    public void testStringLiteralExpression() {
        String input = "\"Hello world\";";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        StringLiteral literal = (StringLiteral) stmt.getExpression();
        assertEquals(literal.getValue(), "Hello world");
    }

    @Test
    public void testParsingArrayLiterals() {
        final String input = "[1, 2 * 2, 3 + 3]";

        final Lexer l = new Lexer(input);
        final Parser p = new Parser(l);
        final Program program = p.parseProgram();
        checkParserErrors(p);


        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        ArrayLiteral array = (ArrayLiteral) stmt.getExpression();
        final Expression[] elems = array.getElements();
        assertEquals(elems.length, 3);
        testIntegerLiteral(elems[0], 1);
        testInfixExpression(elems[1], 2,"*", 2);
        testInfixExpression(elems[2], 3, "+", 3);

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
