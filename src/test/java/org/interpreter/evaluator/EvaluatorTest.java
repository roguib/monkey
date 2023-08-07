package org.interpreter.evaluator;

import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    // For now, we only support "-" arithmetic operator
    @Test
    public void testEvalIntegerExpression() {
        final String[] input = {
                "5",
                "10",
                "-5",
                "-10",
                "5 + 5 + 5 + 5 - 10",
                "2 * 2 * 2 * 2 * 2",
                "-50 + 100 - 50",
                "5 * 2 + 10",
                "5 + 2 * 10",
                "20 + 2 * -10",
                "50 / 2 * 2 + 10",
                "2 * (5 + 10)",
                "3 * 3 * 3 + 10",
                "3 * (3 * 3) + 10",
                "(5 + 10 * 2 + 15 / 3) * 2 + -10",
        };
        final int[] expected = {
                5,
                10,
                -5,
                -10,
                10,
                32,
                0,
                20,
                25,
                0,
                60,
                30,
                37,
                37,
                50,
        };

        assertEquals(input.length, expected.length);
        for(int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            testIntegerObject(evaluated, expected[i]);
        }
    }

    @Test
    public void testEvalBooleanExpression() {
        final String[] input = {
                "true",
                "false",
                "1 < 2",
                "1 > 2",
                "1 < 1",
                "1 > 1",
                "1 == 1",
                "1 != 1",
                "1 == 2",
                "1 != 2",
                "true == true",
                "false == false",
                "true == false",
                "true != false",
                "false != true",
                "(1 < 2) == true",
                "(1 < 2) == false",
                "(1 > 2) == true",
                "(1 > 2) == false",
        };
        final boolean[] expected = {
                true,
                false,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                true,
                true,
                true,
                false,
                true,
                true,
                true,
                false,
                false,
                true,
        };

        assertEquals(input.length, expected.length);
        for(int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            testBooleanObject(evaluated, expected[i]);
        }
    }

    @Test
    public void testBangOperator() {
        final String[] input = {
                "!true",
                "!false",
                "!5", // !5 acts as truthy value
                "!!true",
                "!!false",
                "!!5",
        };
        final boolean[] expected = {
                false,
                true,
                false,
                true,
                false,
                true,
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            testBooleanObject(evaluated, expected[i]);
        }
    }

    @Test
    public void testIfElseExpressions() {
        // everything truthy (not false and not null) will make the if branch to be evaluated
        final String[] input = {
                "if (true) { 10 }",
                "if (false) { 10 }",
                "if (1) { 10 }", // !5 acts as truthy value
                "if (1 < 2) { 10 }",
                "if (1 > 2) { 10 }",
                "if (1 > 2) { 10 } else { 20 }",
                "if (1 < 2) { 10 } else { 20 }",
        };
        final Integer[] expected = {
                10,
                null,
                10,
                10,
                null,
                20,
                10,
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            if (expected[i] != null) {
                testIntegerObject(evaluated, expected[i]);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    public void testReturnValue() {
        final String ifElseReturn = readProgram("src/test/resources/fixtures/if-else-return.monkey");

        final String[] input = {
                "return 10;",
                "return 10; 9;",
                "return 2 * 5; 9;", // !5 acts as truthy value
                "9; return 2 * 5; 9;",
                ifElseReturn,
        };
        final Integer[] expected = {
                10,
                10,
                10,
                10,
                10,
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            testIntegerObject(evaluated, expected[i]);
        }
    }

    @Test
    public void testErrorHandling() {
        final String ifElseBooleanAdditionError = readProgram("src/test/resources/fixtures/if-else-boolean-addition-error.monkey");

        final String[] input = {
                "5 + true;",
                "5 + true; 5;",
                "-true",
                "true + false;",
                "5; true + false; 5",
                "if (10 > 1) { true + false; }",
                ifElseBooleanAdditionError,
        };
        final String[] expected = {
                "type mismatch: INTEGER + BOOLEAN",
                "type mismatch: INTEGER + BOOLEAN",
                "unknown operator: -BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MError evaluated = (MError) testEval(input[i]);
            assertEquals(evaluated.getMessage(), expected[i]);
        }
    }

    private MObject testEval(final String input) {
        final Lexer l = new Lexer(input);
        final Parser parser = new Parser(l);
        final Program program = parser.parseProgram();

        return Evaluator.eval(program);
    }

    private void testIntegerObject(final MObject obj, int expected) {
        final MInteger result = (MInteger) obj;
        assertEquals(result.getValue(), expected);
    }

    private void testBooleanObject(final MObject obj, boolean expected) {
        final MBoolean result = (MBoolean) obj;
        assertEquals(result.getValue(), expected);
    }

    private void testNullObject(final MObject obj) {
        assertTrue(obj instanceof MNull);
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