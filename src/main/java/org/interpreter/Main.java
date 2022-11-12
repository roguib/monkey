package org.interpreter;

import org.interpreter.repl.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to monkey programming language interactive mode (REPL)");
        Repl.start();
    }
}