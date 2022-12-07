package org.interpreter.ast;

import org.interpreter.lexer.Token;

public interface PrefixParse {
    public Expression prefixParse(final Token token);
}
