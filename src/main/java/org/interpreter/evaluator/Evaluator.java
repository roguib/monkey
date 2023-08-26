package org.interpreter.evaluator;

import org.interpreter.ast.*;
import org.interpreter.ast.Boolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Evaluator {

    private static final MBoolean TRUE = new MBoolean(true);
    private static final MBoolean FALSE = new MBoolean(false);
    private static final MNull NULL = new MNull();

    private static final HashMap<String, Builtin> builtins = new HashMap<>();
    static {
        BuiltinInterface lenFunc = (MObject ...args) -> {
            if (args.length != 1) {
                return new MError("wrong number of arguments. got=" + args.length + ", want=1");
            }
            if (args[0] instanceof MString) {
                return new MInteger(((MString) args[0]).getValue().length());
            }
            return new MError("argument to `len` not supported, got " + args[0].type());
        };
        builtins.put("len", new Builtin(lenFunc));
    }
    
    public static MObject eval(Node node, Environment env) {
        // could we use a better pattern than just adding here else if cases ??
        // maybe a factory pattern with generics
        if (node instanceof Program) {
            return evalProgram(((Program) node).getStatements(), env);
        }
        else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).getExpression(), env);
        }
        else if (node instanceof IntegerLiteral) {
            return new MInteger(((IntegerLiteral) node).getValue());
        }
        else if (node instanceof Boolean) {
            return nativeBoolToBooleanObject(((Boolean) node).getValue());
        }
        else if (node instanceof PrefixExpression) {
            // right might be MInteger or MBoolean
            final MObject right = eval(((PrefixExpression) node).getRight(), env);
            if (isError(right)) {
                return right;
            }
            return evalPrefixExpression(((PrefixExpression) node).getOperator(), right);
        }
        else if (node instanceof InfixExpression) {
            final MObject left = eval(((InfixExpression) node).getLeft(), env);
            if (isError(left)) {
                return left;
            }
            final MObject right = eval(((InfixExpression) node).getRight(), env);
            if (isError(right)) {
                return right;
            }
            return evalInfixExpression(((InfixExpression) node).getOperator(), left, right);
        }
        else if (node instanceof BlockStatement) {
            return evalBlockStatement((BlockStatement) node, env);
        }
        else if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression) node, env);
        }
        else if (node instanceof ReturnStatement) {
            final MObject val = eval(((ReturnStatement) node).getReturnValue(), env);
            if (isError(val)) {
                return val;
            }
            return new ReturnValue(val);
        }
        else if (node instanceof LetStatement) {
            final MObject val = eval(((LetStatement) node).getValue(), env);
            if (val instanceof MError) {
                return val;
            }
            env.set(((LetStatement) node).getName().getValue(), val);
        }
        else if (node instanceof Identifier) {
            return (MObject) evalIdentifier((Identifier) node, env);
        }
        else if (node instanceof FunctionLiteral) {
            final Identifier[] params = ((FunctionLiteral) node).getParameters();
            return new MFunction(new ArrayList<>(Arrays.asList(params)), ((FunctionLiteral) node).getBody(), env);
        }
        else if (node instanceof CallExpression) {
            MObject function = eval(((CallExpression) node).getFunction(), env);
            if (isError(function)) {
                return function;
            }
            MObject[] args = evalExpressions(((CallExpression) node).getArguments(), env);
            if (args.length == 1 && isError(args[0])) {
                return args[0];
            }
            return applyFunction(function, args);
        }
        else if (node instanceof StringLiteral) {
            return new MString(((StringLiteral) node).getValue());
        }
        else if (node instanceof ArrayLiteral) {
            MObject[] elements = evalExpressions(((ArrayLiteral) node).getElements(), env);
            if (elements.length == 1 && isError(elements[0])) {
                return elements[0];
            }
            return new MArray(elements);
        }
        return null;
    }
    
    private static MObject evalProgram(final ArrayList<Statement> stmts, Environment env) {
        MObject result = null;

        for (Statement stmt : stmts) {
            result = eval(stmt, env);

            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).getValue();
            }
            else if (result instanceof MError) {
                return result;
            }
        }
        
        return result;
    }

    private static MObject evalBlockStatement(final BlockStatement block, Environment env) {
        MObject result = null;

        for (Statement stmt : block.getStatements()) {
            result = eval(stmt, env);

            if (result != null &&
                (result.type() == MObjectType.RETURN_VALUE || result.type() == MObjectType.ERROR)) {
                return result;
            }
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
        else if ("-".equals(operator)) {
            return evalMinusPrefixOperatorExpression(right);
        }
        return new MError("unkown operator: " + operator + " " + right.type());
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

    private static MObject evalMinusPrefixOperatorExpression(final MObject right) {
        if (right.type() != MObjectType.INTEGER) {
            return new MError("unknown operator: -" + right.type());
        }
        final int value = ((MInteger)right).getValue();
        return new MInteger(-value);
    }

    private static MObject evalInfixExpression(final String operator, final MObject left, final MObject right) {
        if (left.type() == MObjectType.INTEGER && right.type() == MObjectType.INTEGER) {
            return evalIntegerInfixExpression(operator, (MInteger) left, (MInteger) right);
        }

        switch (operator) {
            case "==":
                return nativeBoolToBooleanObject(left == right);
            case "!=":
                return nativeBoolToBooleanObject(left != right);
            default:
                if (left.type() != right.type()) {
                    return new MError("type mismatch: " + left.type() + " " + operator + " " + right.type());
                }
                if (left.type() == MObjectType.STRING && right.type() == MObjectType.STRING) {
                    return evalStringInfixExpression(operator, left, right);
                }
                return new MError("unknown operator: " + left.type() + " " + operator + " " + right.type());
        }
    }

    private static MObject evalIntegerInfixExpression(final String operator, final MInteger left, final MInteger right) {
        final int leftVal = left.getValue();
        final int rightVal = right.getValue();

        switch (operator) {
            case "+":
                return new MInteger(leftVal + rightVal);
            case "-":
                return new MInteger(leftVal - rightVal);
            case "*":
                return new MInteger(leftVal * rightVal);
            case "/":
                return new MInteger(leftVal / rightVal);
            case "<":
                return nativeBoolToBooleanObject(leftVal < rightVal);
            case ">":
                return nativeBoolToBooleanObject(leftVal > rightVal);
            case "==":
                return nativeBoolToBooleanObject(leftVal == rightVal);
            case "!=":
                return nativeBoolToBooleanObject(leftVal != rightVal);
            default:
                return new MError("unknown operator: " + left.type() + " " + operator + " " + right.type());
        }
    }

    private static MObject evalIfExpression(final IfExpression ifExpr, Environment env) {
        final MObject condition = eval(ifExpr.getCondition(), env);
        if (isError(condition)) {
            return condition;
        }
        final BlockStatement alternative = ifExpr.getAlternative();

        if (isTruthy(condition)) {
            return eval(ifExpr.getConsequence(), env);
        } else if (alternative != null) {
            return eval(alternative, env);
        } else {
            return NULL;
        }
    }

    private static MObject evalIdentifier(final Identifier node, final Environment env) {
        final MObject val = env.get(node.getValue());
        if (!(val instanceof MNull)) {
            return val;
        }

        final BuiltinInterface builtin = builtins.get(node.getValue());
        if (builtin != null) {
            return (MObject) builtin;
        }

        return new MError("identifier not found: " + node.getValue());
    }

    private static MObject[] evalExpressions(final Expression[] exps, final Environment env) {
        ArrayList<MObject> result = new ArrayList<>();
        for (final Expression exp: exps) {
            MObject evaluated = eval(exp, env);
            if (isError(evaluated)) {
                return new MObject[]{evaluated};
            }
            result.add(evaluated);
        }
        return result.toArray(new MObject[result.size()]);
    }

    private static MObject applyFunction(MObject fn, MObject[] args) {
        if (fn instanceof MFunction) {
            MFunction function = (MFunction) fn;
            final Environment extendedEnvironment = extendFunctionEnv(function, args);
            MObject evaluated = eval(function.getBody(), extendedEnvironment);
            return unwrapReturnValue(evaluated);
        }
        else if (fn instanceof BuiltinInterface) {
            BuiltinInterface builtin = (BuiltinInterface) fn;
            return builtin.fn(args);
        }
        return new MError("not a function: " + fn.type());
    }

    private static Environment extendFunctionEnv(MFunction fn, MObject[] args) {
        final Environment env = Environment.newEnclosedEnvironment(fn.getEnv());
        final List<Identifier> params = fn.getParameters();
        for (int i = 0; i < params.size(); ++i) {
            env.set(params.get(i).getValue(), args[i]);
        }
        return env;
    }

    private static MObject unwrapReturnValue(MObject obj) {
        if (obj instanceof ReturnValue) {
            return ((ReturnValue) obj).getValue();
        }
        return obj;
    }

    private static MObject evalStringInfixExpression(final String operator, MObject left, MObject right) {
        if (!operator.equals("+")) {
            return new MError("unknown operator: " + left.type() + " " + operator + " " + right.type());
        }
        final String leftVal = ((MString) left).getValue();
        final String rightVal = ((MString) right).getValue();
        return new MString(leftVal + rightVal);
    }

    private static boolean isTruthy(final MObject obj) {
        return obj != NULL && obj != FALSE;
    }

    private static boolean isError(final MObject obj) {
        if (obj != null) {
            return obj.type() == MObjectType.ERROR;
        }
        return false;
    }
}
