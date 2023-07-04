package org.interpreter.ast;

import org.interpreter.lexer.Token;

// if (<condition>) <consequence> else <alternative>
public class IfExpression implements Expression {

    private Token token;
    private Expression condition;
    private BlockStatement consequence;
    private BlockStatement alternative;

    IfExpression(Token token) {
        this.token = token;
    }

    @Override
    public void expressionNode() {
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(final Expression condition) {
        this.condition = condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public void setConsequence(final BlockStatement consequence) {
        this.consequence = consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    public void setAlternative(final BlockStatement alternative) {
        this.alternative = alternative;
    }

    @Override
    public String toString() {
        return "if" + condition + " " + consequence.toString() + (alternative.toString() != null ? "else " + alternative : "");
    }
}
