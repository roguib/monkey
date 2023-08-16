package org.interpreter.repl;

import org.interpreter.ast.Parser;
import org.interpreter.ast.Program;
import org.interpreter.evaluator.Environment;
import org.interpreter.evaluator.Evaluator;
import org.interpreter.evaluator.MObject;
import org.interpreter.lexer.Lexer;

import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * REPL stands for Read, Evaluate, Print and Loop, oftentimes called interactive mode.
 */
final public class Repl {
    // final prevents the class to be extended
    // private constructor prevents Repl from being instantiated
    private Repl() {
    }

    public static final String PROMPT = ">> ";

    public static void start() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        final Environment env = new Environment();
        System.out.print(PROMPT);
        while(true) {
            final String line = scanner.nextLine();
            final Lexer lexer = new Lexer(line);
            final Parser parser = new Parser(lexer);
            final Program program = parser.parseProgram();

            if (parser.getErrors().size() > 0) {
                System.out.println("One or more errors have occurred while parsing the program.");
                System.out.println("Aborting");
                // TODO: Better report parsing errors check p.102
                continue;
            }

            MObject evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                System.out.println(evaluated.inspect());
            }

            System.out.print(PROMPT);
        }
    }
}
