package org.interpreter.evaluator;

import org.interpreter.ast.*;
import org.interpreter.ast.Boolean;

import java.util.ArrayList;

public class Evaluator {

    private static final MBoolean TRUE = new MBoolean(true);
    private static final MBoolean FALSE = new MBoolean(false);
    private static final MNull NULL = new MNull();
    
    public static MObject eval(Node node) {
        if (node instanceof Program) {
            return evalStatements(((Program) node).getStatements());
        }
        else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).getExpression());
        }
        else if (node instanceof IntegerLiteral) {
            return new MInteger(((IntegerLiteral) node).getValue());
        }
        else if (node instanceof Boolean) {
            return nativeBoolToBooleanObject(((Boolean) node).getValue());
        }
        else if (node instanceof PrefixExpression) {
            // right might be MInteger or MBoolean
            final MObject right = eval(((PrefixExpression) node).getRight());
            return evalPrefixExpression(((PrefixExpression) node).getOperator(), right);
        }
        return null;
    }
    
    private static MObject evalStatements(final ArrayList<Statement> stmts) {
        MObject result = null;

        for (Statement stmt : stmts) {
            result = eval(stmt);
        }
        
        return result;
    }

    private static MBoolean nativeBoolToBooleanObject(boolean value) {
        if (value) {
            return TRUE;
        }
        return FALSE;
    }

    private static MObject evalPrefixExpression(final String operator, final MObject right) {
        if ("!".equals(operator)) {
            return evalBangOperatorExpression(right);
        }
        return NULL;
    }

    private static MObject evalBangOperatorExpression(final MObject right) {
        if (right == TRUE) {
            return FALSE;
        }
        else if (right == FALSE) {
            return TRUE;
        }
        else if (right == NULL) {
            return TRUE;
        }
        return FALSE;
    }
}
