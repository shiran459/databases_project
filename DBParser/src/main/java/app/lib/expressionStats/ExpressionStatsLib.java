package app.lib.expressionStats;

import app.lib.WordLib;
import app.utils.ConnectionManager;
import app.utils.Expression;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class ExpressionStatsLib {
    public static ExpressionStats calculateExpressionStats(Expression expression) throws Exception {
        Date createDate = calculateCreateDate(expression);
        int numOfWords = calculateNumOfWords(expression);
        int length = calculateLength(expression);

        return new ExpressionStats(expression, createDate, numOfWords, length);
    }

    private static Date calculateCreateDate(Expression expression) throws Exception {
        String sql =
                "SELECT creation_date " +
                        "FROM expressions    " +
                        "WHERE expression_id = ? ";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, expression.expressionId);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        Date date = null;
        if (res.next()) {
            date = res.getDate(1);
        }

        //Close resources
        pstmt.close();
        res.close();

        return date;
    }

    private static int calculateNumOfWords(Expression expression) {
        return expression.wordIdList.size();
    }

    private static int calculateLength(Expression expression) {
        return expression.value.length();
    }
}
