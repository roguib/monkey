package org.interpreter.evaluator;

import org.interpreter.ast.*;

import java.util.ArrayList;

public class Evaluator {
    
    public static org.interpreter.evaluator.Object eval(Node node) {
        if (node instanceof Program) {
            return evalStatements(((Program) node).getStatements());
        }
        else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).getExpression());
        }
        else if (node instanceof IntegerLiteral) {
            return new org.interpreter.evaluator.Integer(((IntegerLiteral) node).getValue());
        }
        return null;
    }
    
    private static org.interpreter.evaluator.Object evalStatements(final ArrayList<Statement> stmts) {
        org.interpreter.evaluator.Object result = null;
        
        for (int i = 0; i < stmts.size(); ++i) {
            result = eval(stmts.get(i));
        }
        
        return result;
    }
}
