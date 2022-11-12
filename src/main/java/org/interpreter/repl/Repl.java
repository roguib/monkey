package org.interpreter.repl;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;

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
        System.out.print(PROMPT);
        while(true) {
            final String line = scanner.nextLine();
            final Lexer lexer = new Lexer(line);
            Token token;
            do {
                token = lexer.nextToken();
                System.out.println(token);
            } while (!token.equals(new Token(TokenType.EOF, "")));
            System.out.print(PROMPT);
        }
    }
}
