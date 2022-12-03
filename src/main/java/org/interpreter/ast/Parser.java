package org.interpreter.ast;

import org.interpreter.lexer.Lexer;
import org.interpreter.lexer.Token;
import org.interpreter.lexer.TokenType;

import javax.swing.plaf.nimbus.State;

public class Parser {
    private Lexer l;
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
                return null;
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
}
