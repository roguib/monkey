package org.interpreter.evaluator;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.interpreter.ast.Identifier;
import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.evaluator.object.HashKey;
import org.interpreter.evaluator.object.HashPair;
import org.interpreter.evaluator.object.MHash;
import org.interpreter.lexer.Lexer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
                "foobar",
                "\"Hello\" - \"World\"",
                "[1, 2, 3][\"4\"]",
        };
        final String[] expected = {
                "type mismatch: INTEGER + BOOLEAN",
                "type mismatch: INTEGER + BOOLEAN",
                "unknown operator: -BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "unknown operator: BOOLEAN + BOOLEAN",
                "identifier not found: foobar",
                "unknown operator: STRING - STRING",
                "index operator not supported: STRING",
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MError evaluated = (MError) testEval(input[i]);
            assertEquals(evaluated.getMessage(), expected[i]);
        }
    }

    @Test
    public void testLetStatements() {
        final String[] input = {
                "let a = 5; a;",
                "let a = 5 * 5; a;",
                "let a = 5; let b = a; b;",
                "let a = 5; let b = a; let c = a + b + 5; c;"
        };
        final int[] expected = {
                5,
                25,
                5,
                15,
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            testIntegerObject(testEval(input[i]), expected[i]);
        }
    }

    @Test
    public void testFunctionObject() {
        final String input = "fn(x) { x + 2; };";

        final MObject evaluated = testEval(input);
        final MFunction fn = (MFunction) evaluated;
        final ArrayList<Identifier> parameters = fn.getParameters();
        assertEquals(parameters.size(), 1);
        assertEquals(parameters.get(0).toString(), "x");
        assertEquals(fn.getBody().toString(), "(x + 2)");
    }

    @Test
    public void testFunctionApplication() {
        final String[] input = {
                "let identity = fn(x) { x; }; identity(5);",
                "let identity = fn(x) { return x; }; identity(5);",
                "let double = fn(x) { x * 2; }; double(5);",
                "let add = fn(x, y) { x + y; }; add(5, 5);",
                "let add = fn(x, y) { x + y; }; add(5+ 5, add(5, 5));",
                "fn(x) { x; }(5)",
        };
        final int[] expected = {
                5,
                5,
                10,
                10,
                20,
                5,
        };
        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            testIntegerObject(testEval(input[i]), expected[i]);
        }
    }

    @Test
    public void testClosures() {
        final String input = readProgram("src/test/resources/fixtures/closures.monkey");
        testIntegerObject(testEval(input), 4);
    }

    /** @Test
    @Disabled
    public void testGCollectedByJava() {
        // When condition if (x > 550) is 550 or higher, Java throws StackOverflowError
        final String input = readProgram("src/test/resources/fixtures/gc-performed-by-java.monkey");
        testEval(input);
    }*/

    @Test
    public void testStringLiteral() {
        final String input = "\"Hello World!\"";

        final MObject evaluated = testEval(input);
        MString str = (MString) evaluated;
        assertEquals(str.getValue(), "Hello World!");
    }

    @Test
    public void testStringConcatenation() {
        final String input ="\"Hello\" + \" \" + \"World!\"";

        final MObject evaluated = testEval(input);
        MString str = (MString) evaluated;
        assertEquals(str.getValue(), "Hello World!");
    }

    @Test
    public void testBuiltinFunctions() {
        final String[] input = {
                "len(\"\")",
                "len(\"four\")",
                "len(\"hello world\")",
                "len(1)",
                "len(\"one\", \"two\")",
                "len([1])",
                "len([1, 2, 3])",
                "first([])",
                "first([1])",
                "first([1, 2, 3])",
                "last([])",
                "last([1])",
                "last([1, 2, 3])",
                "rest([])",
                "rest([1])",
                "rest([1, 2, 3])",
                "let a = [1, 2]; let b = push(a, 5); b;"
        };
        final MObject[] expected = {
                new MInteger(0),
                new MInteger(4),
                new MInteger(11),
                new MError("argument to `len` not supported, got INTEGER"),
                new MError("wrong number of arguments. got=2, want=1"),
                new MInteger(1),
                new MInteger(3),
                new MNull(),
                new MInteger(1),
                new MInteger(1),
                new MNull(),
                new MInteger(1),
                new MInteger(3),
                new MNull(),
                new MArray(new MObject[0]),
                new MArray(new MObject[]{ new MInteger(2), new MInteger(3)}),
                new MArray(new MObject[]{ new MInteger(1), new MInteger(2), new MInteger(5)}),
        };

        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            if (expected[i] instanceof MInteger) {
                testIntegerObject(evaluated, ((MInteger) expected[i]).getValue());
            }
            else if (expected[i] instanceof MString) {
                assertEquals(((MString) evaluated).getValue(), ((MString) expected[i]).getValue());
            }
            else if (expected[i] instanceof MArray) {
                testArrayObject(evaluated, expected[i]);
            }
            else if (expected[i] instanceof MError) {
                assertEquals(((MError) evaluated).getMessage(), ((MError) expected[i]).getMessage());
            }
        }
    }

    @Test
    public void testArrayLiterals() {
        final String input = "[1, 2 * 2, 3 + 3]";

        final MObject evaluated = testEval(input);
        MArray result = (MArray) evaluated;
        final MObject[] elems = result.getElements();
        assertEquals(elems.length, 3);
        testIntegerObject(elems[0], 1);
        testIntegerObject(elems[1], 4);
        testIntegerObject(elems[2], 6);
    }

    @Test
    public void testArrayIndexExpressions() {
        final String[] input = {
                "[1, 2, 3][0]",
                "[1, 2, 3][1]",
                "[1, 2, 3][2]",
                "let i = 0; [1][i];",
                "[1, 2, 3][1 + 1]",
                "let myArray = [1, 2, 3]; myArray[2];",
                "let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];",
                "let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]",
                "[1, 2, 3][3]",
                "[1, 2, 3][-1]",
        };
        final Integer[] expected = {
                1,
                2,
                3,
                1,
                3,
                3,
                6,
                2,
                null,
                null,
        };

        assertEquals(input.length, expected.length);
        for (int i = 0; i < input.length; ++i) {
            final MObject evaluated = testEval(input[i]);
            if (evaluated instanceof MInteger) {
                testIntegerObject(evaluated, expected[i]);
            }
            else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    public void testHashLiterals() {
        final String input =
            "{" +
                "\"one\": 10 - 9," +
                "\"two\": 1 + 1," +
                "\"three\": 6 / 2," +
                "4: 4," +
                "true: 5," +
                "false: 6" +
            "}";

        final MObject evaluated = testEval(input);
        final MHash result = (MHash) evaluated;

        final HashMap<HashKey, Integer> expected = new HashMap<>();
        expected.put(new MString("one").getHashKey(), 1);
        expected.put(new MString("two").getHashKey(), 2);
        expected.put(new MString("three").getHashKey(), 3);
        expected.put(new MInteger(4).getHashKey(), 4);
        expected.put(new MBoolean(true).getHashKey(), 5);
        expected.put(new MBoolean(false).getHashKey(), 6);

        final  HashMap<HashKey, HashPair> hashPairs = result.getPairs();
        assertEquals(hashPairs.size(), expected.size());

        for (Map.Entry<HashKey, Integer> entry : expected.entrySet()) {
            assertTrue(hashPairs.containsKey(entry.getKey()));
            testIntegerObject(hashPairs.get(entry.getKey()).value, entry.getValue());
        }
    }

    private MObject testEval(final String input) {
        final Lexer l = new Lexer(input);
        final Parser parser = new Parser(l);
        final Program program = parser.parseProgram();
        final Environment env = new Environment();

        return Evaluator.eval(program, env);
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

    private void testArrayObject(final MObject obj, final MObject expected) {
        assertTrue(obj instanceof MArray);
        assertTrue(expected instanceof MArray);
        final MObject[] actArr = ((MArray) obj).getElements();
        final MObject[] expectedArr = ((MArray) expected).getElements();
        assertEquals(actArr.length, expectedArr.length);
        for (int i = 0; i < actArr.length; ++i) {
            assertTrue(actArr[i].inspect().equals(expectedArr[i].inspect()));
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
