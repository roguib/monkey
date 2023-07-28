package org.interpreter.evaluator;

import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    @Test
    public void testEvalIntegerExpression() {
        final String[] input = { "5", "10" };
        final int[] expected = { 5, 10 };

        assertEquals(input.length, expected.length);
        for(int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            testIntegerObject(evaluated, expected[i]);
        }
    }

    @Test
    public void testEvalBooleanExpression() {
        final String[] input = { "true", "false" };
        final boolean[] expected = { true, false };

        assertEquals(input.length, expected.length);
        for(int i = 0; i < input.length; ++i) {
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
