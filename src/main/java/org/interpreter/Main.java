package org.interpreter;

import org.interpreter.lexer.*;

public class Main {
    public static void main(String[] args) {
        final Token token = new Token(TokenType.ILLEGAL, "5");
        System.out.println(token);
    }
}