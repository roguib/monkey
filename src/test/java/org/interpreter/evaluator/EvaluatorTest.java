package org.interpreter.evaluator;

import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    // For now we only support "-" arithmetic operator
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
}
