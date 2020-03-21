package app.lib;

import app.lib.expressionStats.ExpressionStatsLib;
import app.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ExpressionLib {

    public static Expression createExpression(String string, int userId) throws SQLException {
        List<String> wordStrings = Arrays.asList(string.split("\\W+"));
        List<Word> words = WordLib.getWordsFromStrings(wordStrings);

        Map<String, Word> wordMap = new HashMap<>();
        for(Word word : words)
            wordMap.put(word.value, word);

        List<Integer> idList = new ArrayList<>();
        for (String str: wordStrings){
            if (wordMap.containsKey(str))
                idList.add(wordMap.get(str).id);
            else
                idList.add(-1);
        }
        java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());

        return new Expression(-1, userId, idList, string, creationDate);
    }

    public static void insertExpression(Expression expression) throws SQLException {
        String sql = "INSERT INTO expressions(expression_id, user_id, word_id_list, value, creation_date) " +
                "VALUES (expression_seq.NEXTVAL, ?, ?, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, expression.userId);
        pstmt.setString(2, paresWordIdStringFromInts(expression.wordIdList));
        pstmt.setString(3, expression.value);
        pstmt.setDate(4, expression.creationDate);
        pstmt.executeUpdate();

        pstmt.close();
    }

    private static boolean isValidExpressionStart(int start, List<Integer> ithWordLocations, int offset) {
        for (int location : ithWordLocations)
            if (start + offset == location)
                return true;

        return false;
    }

    public static Map<Article, List<Integer>> searchExpressionInDB(int expressionId) throws SQLException {

        Expression expression = getExpressionsById(expressionId);
        List<Integer> articleIdList = ArticleLib.searchArticlesByWordsIds(expression.wordIdList);
        Map<Article, List<Integer>> articleMap = new HashMap<>();

        for (int articleId : articleIdList) {
            List<Integer> locations = searchExpressionInArticle(expressionId, articleId);
            if (!locations.isEmpty()) {
                articleMap.put(ArticleLib.getArticleById(articleId), locations);
            }
        }

        return articleMap;
    }

    /**
     * Returns a list of all locations (by word offset) of an expression in a given article.
     *
     * @param expressionId ID of the expression searched.
     * @param articleId    ID of the article to be searched in.
     * @return A list of all locations (by word offset) of an expression in a given article
     * @throws SQLException If a transaction has failed.
     */
    private static List<Integer> searchExpressionInArticle(int expressionId, int articleId) throws SQLException {
        List<Integer> expressionWords = getExpressionWordIdList(expressionId);

        List<Integer> potentialExpressionLocations = WordLib.searchWordLocationsByArticle(expressionWords.get(0), articleId); //Locations of the first word in the word ID list

        if (potentialExpressionLocations.isEmpty())
            return potentialExpressionLocations;

        for (int i = 1; i < expressionWords.size(); i++) {
            List<Integer> currWordLocations = WordLib.searchWordLocationsByArticle(expressionWords.get(i), articleId); //Locations of i'th word

            List<Boolean> flags = new ArrayList<>();
            for (int expressionStart : potentialExpressionLocations)
                flags.add(isValidExpressionStart(expressionStart, currWordLocations, i));

            for (int j = flags.size() - 1; j >= 0; j--)
                if (!flags.get(j))
                    potentialExpressionLocations.remove(j);
            if (potentialExpressionLocations.isEmpty())
                break;
        }

        return potentialExpressionLocations;
    }

    public static List<Expression> getExpressions(int userId) throws Exception {
        ResultSet res = null;
        String sql = "SELECT expression_id, value, word_id_list, creation_date " +
                "FROM expressions " +
                "WHERE user_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, userId);
        res = pstmt.executeQuery();

        List<Expression> expressions = new ArrayList<>();

        while (res.next()) {
            int expression_id = res.getInt("expression_id");
            String value = res.getString("value");
            String word_id_list = res.getString("word_id_list");
            java.sql.Date creationDate = res.getDate("creation_date");
            Expression expression = new Expression(expression_id, userId, paresWordIdList(word_id_list), value, creationDate);
            expression.stats = ExpressionStatsLib.calculateExpressionStats(expression);
            expressions.add(expression);
        }
        //Close resources
        pstmt.close();
        res.close();

        return expressions;
    }

    public static Expression getExpressionsById(int expressionId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT user_id, value, word_id_list, creation_date " +
                "FROM expressions " +
                "WHERE expression_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, expressionId);
        res = pstmt.executeQuery();

        Expression expression = null;

        if (res.next()) {
            int userId = res.getInt("user_id");
            String value = res.getString("value");
            String word_id_list = res.getString("word_id_list");
            java.sql.Date creationDate = res.getDate("creation_date");

            expression = new Expression(expressionId, userId, paresWordIdList(word_id_list), value, creationDate);
        }
        //Close resources
        pstmt.close();
        res.close();

        return expression;
    }

    static List<Integer> getExpressionWordIdList(int expressionId)
            throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id_list " +
                "FROM expressions " +
                "WHERE expression_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, expressionId);
        res = pstmt.executeQuery();

        //Read the result
        List<Integer> wordIdList = new ArrayList<>();
        if (res.next()) {
            String wordIdListString = res.getString("word_id_list");
            wordIdList = paresWordIdList(wordIdListString);
        }

        pstmt.close();
        res.close();

        return wordIdList;
    }

    public static List<Integer> paresWordIdList(String idList) {
        String[] ids = idList.split(",");
        List<Integer> idsInt = new ArrayList<>();
        for (String id : ids) {
            idsInt.add(Integer.parseInt(id));
        }

        return idsInt;
    }

    public static String paresWordIdStringFromInts(List<Integer> wordIds) {
        return wordIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
