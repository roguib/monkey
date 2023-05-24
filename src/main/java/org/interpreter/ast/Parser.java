package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private Lexer l;
    private ArrayList<String> errors = new ArrayList<>();

    // establishes the precedence of each operator over the next one
    private HashMap<String, Integer> operationPrecedence = new HashMap<>();
    {
        // TODO: should also be an enum like TokenType??
        operationPrecedence.put("LOWEST", 1);
        operationPrecedence.put("EQUALS", 2); // ==
        operationPrecedence.put("LESSGREATER", 3); // > or <
        operationPrecedence.put("SUM", 4); // +
        operationPrecedence.put("PRODUCT", 5); // *
        operationPrecedence.put("PREFIX", 6); // -x or !x
        operationPrecedence.put("CALL", 7); // myFunction()
    }

    /**
     * maintains a map of <TokenType, PrefixParse> so given a TokenType
     * we can retrieve it's parsing function
     */
    private HashMap<TokenType, PrefixParse> prefixParseFns = new HashMap<>();
    {
        // This would look much better with functional programming but we aren't there yet
        prefixParseFns.put(TokenType.IDENT, (Token token) -> new Identifier(token, token.getLiteral()));
        prefixParseFns.put(TokenType.INT, (Token token) -> {
            final IntegerLiteral lit = new IntegerLiteral(token);
            try {
                int value = Integer.parseInt(token.getLiteral());
                lit.setValue(value);
            } catch (NumberFormatException e) {
                errors.add("Couldn't parse " + token.getLiteral() + " as integer");
                return null;
            }
            return lit;
        });
        final PrefixParse p = (Token token) -> {
            PrefixExpression exp = new PrefixExpression(token, token.getLiteral());

            nextToken();
            exp.setRight(parseExpression(operationPrecedence.get("LOWEST")));
            return exp;
        };
        prefixParseFns.put(TokenType.BANG, p);
        prefixParseFns.put(TokenType.MINUS, p);
    }

    private HashMap<TokenType, InfixParse> infixParseFns;

    /**
     * curToken and peekToken act as a pointers as our lexer has:
     * position and peekPosition
     */
    private Token curToken;
    private Token peekToken;

    public Parser(final Lexer l) {
        this.l = l;

        nextToken();
        nextToken();
    }

    public void nextToken() {
        curToken = peekToken;
        peekToken = l.nextToken();
    }

    public Program parseProgram() {
        final Program program = new Program();

        while (curToken.getType() != TokenType.EOF) {
            final Statement stmt = parseStatement();
            if (stmt != null) {
                program.addStatement(stmt);
            }
            nextToken();
        }

        return program;
    }

    private Statement parseStatement() {
        switch (curToken.getType()) {
            case LET:
                return parseLetStatement();
            case RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    private LetStatement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken);

        if (!expectPeek(TokenType.IDENT)) {
            return null;
        }

        stmt.setName(new Identifier(curToken, curToken.getLiteral()));

        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        // TODO: Skipping expressions until we encounter semicolon
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);

        nextToken();

        // TODO: Skipping expressions until we encounter semicolon
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    private Statement parseExpressionStatement() {
        Statement stmt = new ExpressionStatement(
            curToken,
            parseExpression(operationPrecedence.get("LOWEST"))
        );

        // check for optional semicolon
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    private Expression parseExpression(int precedence) {
        final PrefixParse prefix = prefixParseFns.get(curToken.getType());
        if (prefix == null) {
            errors.add("No prefix parse function found for " + curToken.getType());
            return null;
        }
        return prefix.prefixParse(curToken);
    }

    private Expression parseIntegerLiteral() {
        final IntegerLiteral lit = new IntegerLiteral(curToken);
        try {
            int value = Integer.parseInt(curToken.getLiteral());
            lit.setValue(value);
        } catch (NumberFormatException e) {
            errors.add("Couldn't parse " + curToken.getLiteral() + " as integer");
            return null;
        }
        return lit;
    }

    private boolean curTokenIs(TokenType t) {
        return curToken.getType() == t;
    }

    private boolean peekTokenIs(TokenType t) {
        return peekToken.getType() == t;
    }

    private boolean expectPeek(TokenType t) {
        if (peekTokenIs(t)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
