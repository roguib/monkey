package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    /**
     * curToken and peekToken act as a pointers as our lexer has:
     * position and peekPosition
     */
    private Token curToken;
    private Token peekToken;
    private Lexer l;
    private ArrayList<String> errors = new ArrayList<>();

    // establishes the precedence of each operator over the next one by associating
    // each token with their precedence
    private enum operationPrecedence {
        LOWEST(1),
        EQUALS(2),
        LESSGREATER(3),
        SUM(4),
        PRODUCT(5),
        PREFIX(6),
        CALL(7);

        private final int value;
        private operationPrecedence(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

    };
    private HashMap<TokenType, Integer> precedences = new HashMap<>();
    {
        precedences.put(TokenType.EQ, operationPrecedence.EQUALS.getValue());
        precedences.put(TokenType.NOT_EQ, operationPrecedence.EQUALS.getValue());
        precedences.put(TokenType.LT, operationPrecedence.LESSGREATER.getValue());
        precedences.put(TokenType.GT, operationPrecedence.LESSGREATER.getValue());
        precedences.put(TokenType.PLUS, operationPrecedence.SUM.getValue());
        precedences.put(TokenType.MINUS, operationPrecedence.SUM.getValue());
        precedences.put(TokenType.SLASH, operationPrecedence.PRODUCT.getValue());
        precedences.put(TokenType.ASTERISK, operationPrecedence.PRODUCT.getValue());
    }

    /**
     * maintains a map of <TokenType, PrefixParse> so given a TokenType
     * we can retrieve it's parsing function
     */
    private HashMap<TokenType, PrefixParse> prefixParseFns = new HashMap<>();
    {
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
            exp.setRight(parseExpression(operationPrecedence.PREFIX.getValue()));
            return exp;
        };
        prefixParseFns.put(TokenType.BANG, p);
        prefixParseFns.put(TokenType.MINUS, p);
        final PrefixParse b = (Token token) -> new Boolean(curToken, curTokenIs(TokenType.TRUE));
        prefixParseFns.put(TokenType.TRUE, b);
        prefixParseFns.put(TokenType.FALSE, b);

        final PrefixParse paren = (Token token) -> {
            nextToken();
            Expression exp = parseExpression(operationPrecedence.LOWEST.getValue());
            if (!expectPeek(TokenType.RPAREN)) {
                return null;
            }
            return exp;
        };
        prefixParseFns.put(TokenType.LPAREN, paren);
    }

    private HashMap<TokenType, InfixParse> infixParseFns = new HashMap<>();
    {
        final InfixParse p = (Expression left) -> {
            final InfixExpression infixExp = new InfixExpression(curToken, curToken.getLiteral(), left);
            final int precedence = curPrecedence();
            nextToken();
            infixExp.setRight(parseExpression(precedence));

            return infixExp;
        };

        infixParseFns.put(TokenType.PLUS, p);
        infixParseFns.put(TokenType.MINUS, p);
        infixParseFns.put(TokenType.SLASH, p);
        infixParseFns.put(TokenType.ASTERISK, p);
        infixParseFns.put(TokenType.EQ, p);
        infixParseFns.put(TokenType.NOT_EQ, p);
        infixParseFns.put(TokenType.LT, p);
        infixParseFns.put(TokenType.GT, p);
    }

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
            parseExpression(operationPrecedence.LOWEST.getValue())
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

        Expression leftExp = prefix.prefixParse(curToken);

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            InfixParse infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }
            nextToken();

            leftExp = infix.infixParse(leftExp);
        }

        return leftExp;
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

    private int peekPrecedence() {
        if (precedences.containsKey(peekToken.getType())) {
            return precedences.get(peekToken.getType());
        }
        return operationPrecedence.LOWEST.getValue();
    }

    private int curPrecedence() {
        if (precedences.containsKey(curToken.getType())) {
            return precedences.get(curToken.getType());
        }
        return operationPrecedence.LOWEST.getValue();
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
