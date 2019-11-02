package app.lib;

import app.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionLib {
    public static void insertExpression(int userId, String wordIdList, String value, int wordCount) throws SQLException {
        java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());
        String sql = "INSERT INTO expressions(expression_id, user_id, word_id_list, value, word_count, creation_date) " +
                "VALUES (NULL, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setString(2, wordIdList);
        pstmt.setString(3, value);
        pstmt.setInt(1, wordCount);
        pstmt.setDate(1, creationDate);
        pstmt.executeUpdate();
    }

    private static boolean isValidExpressionStart(int start, List<Integer> ithWordLocations, int offset){
        for(int location : ithWordLocations)
            if(start + offset == location)
                return true;

        return false;
    }

    /**
     * Returns a list of all locations (by word offset) of an expression in a given article.
     * @param expressionId ID of the expression searched.
     * @param aricleId ID of the article to be searched in.
     * @return A list of all locations (by word offset) of an expression in a given article
     * @throws SQLException If a transaction has failed.
     */
    static List<Integer> searchExpressionInArticle(int expressionId, int aricleId) throws SQLException {
        ArrayList<Integer> expressionWords = getExpressionWordIdList(expressionId);

        List<Integer> potentialExpressionLocations = WordLib.searchWordLocationsInArticle(expressionWords.get(0), aricleId); //Locations of the first word in the word ID list

        if (potentialExpressionLocations.isEmpty())
            return potentialExpressionLocations;

        for (int i = 1; i < expressionWords.size(); i++) {
            List<Integer> currWordLocations = WordLib.searchWordLocationsInArticle(expressionWords.get(i), aricleId); //Locations of i'th word

            List<Boolean> flags = new ArrayList<>();
            for (int expressionStart : potentialExpressionLocations)
                flags.add(isValidExpressionStart(expressionStart, currWordLocations, i));

            for (int j = flags.size() - 1; j >= 0; j--)
                if (!flags.get(j))
                    potentialExpressionLocations.remove(j);
            if(potentialExpressionLocations.isEmpty())
                break;
        }

        return potentialExpressionLocations;
    }

    private static ResultSet getExpressions(int userId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT expression_id, value " +
                "FROM expressions " +
                "WHERE user_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, userId);
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }

    static ArrayList<Integer> getExpressionWordIdList(int expressionId)
            throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id_list " +
                "FROM expressions " +
                "WHERE expression_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, expressionId);
        } catch (SQLException e) {
            throw new SQLException();
        }

        //Read the result
        ArrayList<Integer> wordIdList = new ArrayList<>();
        while (res.next()) {
            int currWordId = res.getInt("word_id_list");
            wordIdList.add(currWordId);
        }

        return wordIdList;
    }
}
