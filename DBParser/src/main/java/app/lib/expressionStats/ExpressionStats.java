package app.lib.expressionStats;

import app.utils.Expression;

import java.util.Date;

public class ExpressionStats {
    Expression expression;
    Date createDate;
    int numOfWords;
    int length;

    public ExpressionStats(Expression expression, Date createDate, int numOfWords, int length) {
        this.expression = expression;
        this.createDate = createDate;
        this.numOfWords = numOfWords;
        this.length = length;
    }

    @Override
    public String toString() {
        return "id: " + expression.expressionId + " createDate: " + createDate + " numOfWords: " + numOfWords + " length: " + length;
    }
}
