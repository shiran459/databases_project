package app.lib;

import app.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticleLib {
    /**
     * Inserts an article into the ARTICLES table.
     *
     * @param title Title of the article.
     * @param path  Path to the HTML file of the article.
     * @throws SQLException If the transaction has failed.
     */
    public static int insertArticle(String title, String path) throws SQLException {
        String sql = "INSERT INTO articles(article_id, article_name, path)" +
                "VALUES (article_seq.NEXTVAL, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql,new String[]{"article_id"});
//        pstmt.setNull(1, java.sql.Types.INTEGER);
        pstmt.setString(1, title);
        pstmt.setString(2, path);
        pstmt.executeUpdate();

        //Get the generated articleId
        ResultSet rs = pstmt.getGeneratedKeys();
        int result = -1;
        if (rs.next()) {
            result = rs.getInt(1);
        }

        //Close resources
        pstmt.close();
        rs.close();;

        return result;
    }

    public static String getArticleTitle(int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT title " +
                "FROM articles " +
                "WHERE article_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, articleId);
        res = pstmt.executeQuery();

        String title;
        if (!res.next()) {
            title = null;
        } else {
            title = res.getString("title");
        }

        //Close resources
        res.close();
        pstmt.close();

        return title;
    }

    public static String getArticlePath(int articleId) throws SQLException {
        //Get the file path
        ResultSet res = null;
        String sql = "SELECT path " +
                "FROM articles " +
                "WHERE article_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, articleId);
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }
        if (!res.next()) {
            return null;
        } else {
            return res.getString("path");
        }
    }

    public static List<String> searchArticlesByTitle(String title) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT title " +
                "FROM articles " +
                "WHERE title LIKE ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, title);
        res = pstmt.executeQuery();

        //Extract results
        List<String> results = new ArrayList<>();
        while (res.next()) {
            results.add(res.getString("title"));
        }

        //Close resource
        res.close();

        return results;
    }

    public static List<String> searchArticlesByCategory(String category) throws SQLException {
        ResultSet res;
        String sql = "SELECT title " +
                "FROM articles_by_category NATURAL JOIN categories " +
                "WHERE category_name = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, category);
        res = pstmt.executeQuery();

        //Extract results
        List<String> results = new ArrayList<String>();
        while (res.next()) {
            results.add(res.getString("title"));
        }

        //Close resources
        res.close();

        return results;
    }

    //TODO: Decide whether to remove
//    public static List<Integer> searchArticlesByWord(String word) throws SQLException {
//        ResultSet res;
//        String sql = "SELECT DISTINCT word_index.article_id " +
//                "FROM word_index NATURAL JOIN words " +
//                "WHERE words.value = ?";
//
//        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
//        pstmt.setString(1, word);
//        res = pstmt.executeQuery();
//
//        //Extract results
//        List<Integer> results = new ArrayList<Integer>();
//        while (res.next()) {
//            results.add(res.getInt("article_id"));
//        }
//
//        //Close resources
//        res.close();
//
//        return results;
//    }

    public static List<Integer> searchArticlesByWords(List<String> words) throws SQLException {
        ResultSet res;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT DISTINCT article_id " +
                "FROM word_index NATURAL JOIN words " +
                "WHERE article_id IN ";
        for (int i = 0; i < words.size(); i++) {
            if (i == words.size() - 1) {
                wordSubQueries.append("(SELECT article_id " +
                        "FROM word_index NATURAL JOIN words " +
                        "WHERE value = ?)");
            } else {
                wordSubQueries.append("(SELECT article_id " +
                        "FROM word_index NATURAL JOIN words " +
                        "value = ?) INTERSECT ");
            }
        }
        sql += "(" + wordSubQueries + ")";

        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            for (int i = 0; i < words.size(); i++) {
                pstmt.setString(i + 1, words.get(i));
            }
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }

        //Extract results
        List<Integer> articleIdList = new ArrayList<Integer>();
        while (res.next()) {
            articleIdList.add(res.getInt("article_id"));
        }

        //Close resources
        res.close();

        return articleIdList;
    }

    public static List<Integer> searchArticlesByWordsIds(List<Integer> containsWords) throws SQLException {
        ResultSet res;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT article_id, title " +
                "FROM word_index NATURAL JOIN articles NATURAL JOIN words " +
                "WHERE article_id IN ";
        for (int i = 0; i < containsWords.size(); i++) {
            if (i == containsWords.size() - 1) {
                wordSubQueries.append("(SELECT article_id FROM word_index WHERE word_id = ?)");
            } else {
                wordSubQueries.append("(SELECT article_id FROM word_index word_id = ?) INTERSECT ");
            }
        }
        sql += "(" + wordSubQueries + ")";

        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            for (int i = 0; i < containsWords.size(); i++) {
                pstmt.setInt(i + 1, containsWords.get(i));
            }
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }

        //Extract results
        List<Integer> articleIds = new ArrayList<>();
        while (res.next()) {
            articleIds.add(res.getInt("article_id"));
        }

        //Close resources
        res.close();

        return articleIds;
    }

    public static List<Integer> searchArticlesByExpression(int expressionId) throws SQLException {
        //Filter by articles which contain all the words in the expression
        List<Integer> wordIdList = ExpressionLib.getExpressionWordIdList(expressionId);
        List<Integer> articleIdList = searchArticlesByWordsIds(wordIdList);
        List<Integer> result = new ArrayList<>();

        //Filter by articles which contain the expression
        for (Integer articleId : articleIdList) {
            List<Integer> expressionLocations = ExpressionLib.searchExpressionInArticle(expressionId, articleId);
            if (!expressionLocations.isEmpty()) {
                result.add(articleId);
            }
        }
        return result;
    }

    public static List<String> getArticleWords(int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT value " +
                "FROM word_index NATURAL JOIN words " +
                "WHERE article_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, articleId);
        res = pstmt.executeQuery();

        //Extract results from result set
        List<String> result = new ArrayList<>();
        while (res.next()) {
            result.add(res.getString("value"));
        }

        //Close resources
        pstmt.close();
        res.close();

        return result;
    }

    public static ResultSet getLocationsByOffset(int wordId, int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT offset " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, wordId);
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }

    public static ResultSet getLocationsByParagraph(int wordId, int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT par_num, par_offset " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, wordId);
            pstmt.setInt(2, articleId);
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }
}
