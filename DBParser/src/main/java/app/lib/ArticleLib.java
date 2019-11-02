package app.lib;

import app.ConnectionManager;

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
        String sql = "INSERT INTO articles(article_id, article_name,path)" +
                "VALUES (?, ? ,?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setNull(1, java.sql.Types.INTEGER);
        pstmt.setString(2, title);
        pstmt.setString(3, path);
        pstmt.executeUpdate();

        ResultSet rs=pstmt.getGeneratedKeys();

        if(rs.next()){
            return rs.getInt(1);
        }
        return -1;
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

    public static ResultSet searchArticlesByTitle(String title) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT article_id, title " +
                "FROM articles " +
                "WHERE title LIKE ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, title);
        res = pstmt.executeQuery();

        return res;
    }

    public static ResultSet searchArticlesByCategory(String category) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT article_id, title " +
                "FROM articles_by_category NATURAL JOIN categories " +
                "WHERE category_name = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, category);
        res = pstmt.executeQuery();

        return res;
    }

    public static List<Integer> getArticlesByWord(String word) throws SQLException {
        ResultSet res;
        String sql = "SELECT DISTINCT word_index.article_id " +
                "FROM word_index NATURAL JOIN words " +
                "WHERE words.value = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, word);
        res = pstmt.executeQuery();

        //Return results to user
        List<Integer> articleIds = new ArrayList<Integer>();
        while (res.next()){
            articleIds.add(res.getInt("article_id"));
        }

        //Close resources
        res.close();

        return articleIds;
    }

    public static ResultSet searchArticlesByWords(ArrayList<Integer> containsWords) throws SQLException {
        ResultSet res = null;
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
        return res;
    }

    public static List<Integer> getArticlesByExpression(int expressionId) throws SQLException{
        ArrayList<Integer> result = new ArrayList<>();

        //Filter by articles which contain all the words in the expression
        ArrayList<Integer> wordIdList = ExpressionLib.getExpressionWordIdList(expressionId);
        ResultSet rs = searchArticlesByWords(wordIdList);

        //Filter by articles which contain the expression
        while(rs.next()){
            int articleId = rs.getInt("article_id");
            List<Integer> expressionLocations = ExpressionLib.searchExpressionInArticle(expressionId, articleId);
            if (expressionLocations.isEmpty()){
                rs.deleteRow();
            }
            else{
                result.add(articleId);
            }
        }

        rs.close();
        return result;
    }

    public static ResultSet getArticleWords(int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id, value " +
                "FROM word_index NATURAL JOIN words " +
                "WHERE article_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, articleId);
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
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
