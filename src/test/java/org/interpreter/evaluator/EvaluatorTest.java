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
            final org.interpreter.evaluator.Object evaluated = testEval(input[i]);
            testIntegerObject(evaluated, expected[i]);
        }
    }

    private org.interpreter.evaluator.Object testEval(final String input) {
        final Lexer l = new Lexer(input);
        final Parser parser = new Parser(l);
        final Program program = parser.parseProgram();

        return Evaluator.eval(program);
    }

    private void testIntegerObject(final org.interpreter.evaluator.Object obj, int expected) {
        final org.interpreter.evaluator.Integer result = (org.interpreter.evaluator.Integer) obj;
        assertEquals(result.getValue(), expected);
    }
}
