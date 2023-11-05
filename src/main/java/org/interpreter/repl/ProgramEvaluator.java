package org.interpreter.repl;

import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.evaluator.Environment;
import org.interpreter.evaluator.Evaluator;
import org.interpreter.evaluator.MError;
import org.interpreter.evaluator.MObject;
import org.interpreter.lexer.Lexer;

public class ProgramEvaluator {
    // This is a public API which some ws might rely on
    public final static String ERROR_PARSE_TOKEN = "One or more errors have occurred while parsing the program. Aborting";
    public final static String ERROR_EVAL_TOKEN = "Error while evaluating the program: ";

    public static String evaluate(final String input) {
        final Environment env = new Environment();

        final Lexer lexer = new Lexer(input);
        final Parser parser = new Parser(lexer);
        final Program program = parser.parseProgram();

        if (parser.getErrors().size() > 0) {
            // TODO: Better report parsing errors check p.102
            return ERROR_PARSE_TOKEN;
        }

        MObject evaluated = Evaluator.eval(program, env);
        if (evaluated != null && !(evaluated instanceof MError)) {
            return evaluated.inspect();
        } else {
            return ERROR_EVAL_TOKEN + evaluated.inspect();
        }
    }
}
