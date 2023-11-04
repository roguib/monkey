package org.interpreter.evaluator;

import org.interpreter.ast.*;
import org.interpreter.ast.Boolean;
import org.interpreter.evaluator.object.HashKey;
import org.interpreter.evaluator.object.HashPair;
import org.interpreter.evaluator.object.Hashable;
import org.interpreter.evaluator.object.MHash;

import java.util.*;

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
            } else if (args[0] instanceof MArray) {
                return new MInteger(((MArray) args[0]).getElements().length);
            }
            return new MError("argument to `len` not supported, got " + args[0].type());
        };
        builtins.put("len", new Builtin(lenFunc));

        BuiltinInterface firstFunction = (MObject ...args) -> {
            if (args.length != 1) {
                return new MError("wrong number of arguments. got=" + args.length + ", want=1");
            }
            if (args[0].type() != MObjectType.ARRAY) {
                return new MError("argument to `first must be ARRAY, got= " + args[0].type());
            }
            MArray arr = (MArray) args[0];
            if (arr.getElements().length > 0) {
                return arr.getElements()[0];
            }
            return NULL;
        };
        builtins.put("first", new Builtin(firstFunction));

        BuiltinInterface lastFunction = (MObject ...args) -> {
            if (args.length != 1) {
                return new MError("wrong number of arguments. got=" + args.length + ", want=1");
            }
            if (args[0].type() != MObjectType.ARRAY) {
                return new MError("argument to `first must be ARRAY, got= " + args[0].type());
            }
            MArray arr = (MArray) args[0];
            // TODO: this could be improved by saving length as a local variable in the Array class
            int length = arr.getElements().length;
            if (length > 0) {
                return arr.getElements()[length - 1];
            }
            return NULL;
        };
        builtins.put("last", new Builtin(lastFunction));

        BuiltinInterface restFunction = (MObject ...args) -> {
            if (args.length != 1) {
                return new MError("wrong number of arguments. got=" + args.length + ", want=1");
            }
            if (args[0].type() != MObjectType.ARRAY) {
                return new MError("argument to `first must be ARRAY, got= " + args[0].type());
            }
            MArray arr = (MArray) args[0];
            int length = arr.getElements().length;
            if (length > 0) {
                return new MArray(Arrays.copyOfRange(arr.getElements(), 1, length));
            }
            return NULL;
        };
        builtins.put("rest", new Builtin(restFunction));

        BuiltinInterface pushFunction = (MObject ...args) -> {
            if (args.length != 2) {
                return new MError("wrong number of arguments, got=" + args.length + ", want=2");
            }
            if (args[0].type() != MObjectType.ARRAY) {
                return new MError("argument to `push` must be ARRAY, got " + args[0].type());
            }
            final MObject[] oldElems = ((MArray) args[0]).getElements();
            final MObject[] newElems = new MObject[oldElems.length + 1];
            for (int i = 0; i < oldElems.length; ++i) {
                newElems[i] = oldElems[i];
            }
            newElems[oldElems.length] = args[1];
            return new MArray(newElems);
        };
        builtins.put("push", new Builtin(pushFunction));
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
        else if (node instanceof IndexExpression) {
            final MObject left = eval(((IndexExpression) node).getLeft(), env);
            if (isError(left)) {
                return left;
            }
            final MObject index = eval(((IndexExpression) node).getIndex(), env);
            if (isError(index)) {
                return index;
            }
            return evalIndexExpression(left, index);
        } else if (node instanceof HashLiteral) {
            return evalHashLiteral((HashLiteral) node, env);
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

    private static MObject evalIndexExpression(MObject left, MObject index) {
        if (left.type() == MObjectType.ARRAY && index.type() == MObjectType.INTEGER) {
            return evalArrayIndexExpression((MArray) left, (MInteger) index);
        } else {
            return new MError("index operator not supported: " + index.type());
        }
    }

    private static MObject evalArrayIndexExpression(MArray array, MInteger index) {
        final Integer idx = index.getValue();
        final int max = array.getElements().length - 1;
        if (idx < 0 || idx > max) {
            // TODO: Handle error properly
            return NULL;
        }
        return array.getElements()[idx];
    }

    private static MObject evalHashLiteral(HashLiteral node, final Environment env) {
        final HashMap<HashKey, HashPair> pairs = new HashMap<>();
        final HashMap<Expression, Expression> expressions = node.getPairs();

        for (Map.Entry<Expression, Expression> entry : expressions.entrySet()) {
            final MObject key = eval(entry.getKey(), env);
            if (key instanceof MError) {
                return key;
            }

            if (!(key instanceof Hashable)) {
                return new MError(key.type() + " cannot be used as a hash key");
            }

            final Hashable hashKey = ((Hashable) key);
            final MObject value = eval(entry.getValue(), env);
            if (value instanceof MError) {
                return value;
            }

            pairs.put(hashKey.getHashKey(), new HashPair(key, value));
        }
        return new MHash(pairs);
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
