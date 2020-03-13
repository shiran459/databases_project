package app.lib;

import app.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
        String sql = "INSERT INTO articles(article_id, title, path)" +
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

    public static Article getArticleById(int articleId) throws SQLException {
        //Get the file path
        ResultSet res = null;
        String sql = "SELECT path, title " +
                "FROM articles " +
                "WHERE article_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, articleId);
        res = pstmt.executeQuery();

        if (!res.next()) {
            pstmt.close();
            return null;
        } else {
            String title = res.getString("title");
            String path = res.getString("path");
            Article article = new Article(articleId, title);
            article.path = path;
            res.close();
            pstmt.close();

            return article;
        }
    }

    public static List<Article> searchArticlesByTitle(String title) throws SQLException {
        String titlePattern = '%' + title + '%';
        ResultSet res = null;
        String sql = "SELECT article_id, title " +
                "FROM articles " +
                "WHERE title LIKE ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, titlePattern);
        res = pstmt.executeQuery();

        //Extract results
        List<Article> results = new ArrayList<>();
        while (res.next()) {
            int articleId = res.getInt("article_id");
            String articleTitle = res.getString("title");
            Article curr = new Article(articleId, articleTitle);
            results.add(curr);
        }

        //Close resource
        res.close();
        pstmt.close();

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
        pstmt.close();

        return results;
    }

    public static List<Article> searchArticlesByWords(List<String> words) throws SQLException {
        ResultSet res;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT DISTINCT article_id, title " +
                "FROM word_index NATURAL JOIN words NATURAL JOIN articles " +
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

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        for (int i = 0; i < words.size(); i++) {
            pstmt.setString(i + 1, words.get(i));
        }
        res = pstmt.executeQuery();


        //Extract results
        List<Article> articles = new ArrayList<>();
        while (res.next()) {
            int articleId = res.getInt("article_id");
            String title = res.getString("title");
            Article article = new Article(articleId,title);
            articles.add(article);
        }

        //Close resources
        res.close();
        pstmt.close();

        return articles;
    }

    public static List<Integer> searchArticlesByWordsIds(List<Integer> containsWords) throws SQLException {
        ResultSet res;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT DISTINCT article_id, title " +
                "FROM word_index NATURAL JOIN articles NATURAL JOIN words " +
                "WHERE article_id IN ";
        for (int i = 0; i < containsWords.size(); i++) {
            if (i == containsWords.size() - 1) {
                wordSubQueries.append("(SELECT article_id FROM word_index WHERE word_id = ?)");
            } else {
                wordSubQueries.append("(SELECT article_id FROM word_index WHERE word_id = ?) INTERSECT ");
            }
        }
        sql += "(" + wordSubQueries + ")";


        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        for (int i = 0; i < containsWords.size(); i++) {
            pstmt.setInt(i + 1, containsWords.get(i));
        }
        res = pstmt.executeQuery();


        //Extract results
        List<Integer> articleIds = new ArrayList<>();
        while (res.next()) {
            articleIds.add(res.getInt("article_id"));
        }

        //Close resources
        res.close();
        pstmt.close();

        return articleIds;
    }

//    public static List<Integer> searchArticlesByExpression(int expressionId) throws SQLException {
//        //Filter by articles which contain all the words in the expression
//        List<Integer> wordIdList = ExpressionLib.getExpressionWordIdList(expressionId);
//        List<Integer> articleIdList = searchArticlesByWordsIds(wordIdList);
//        List<Integer> result = new ArrayList<>();
//
//        //Filter by articles which contain the expression
//        for (Integer articleId : articleIdList) {
//            List<Integer> expressionLocations = ExpressionLib.searchExpressionInArticle(expressionId, articleId);
//            if (!expressionLocations.isEmpty()) {
//                result.add(articleId);
//            }
//        }
//        return result;
//    }

    public static List<ArticleWord> getArticleWords(int articleId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id, value, word_offset, par_num, par_offset " +
                "FROM word_index NATURAL JOIN words " +
                "WHERE article_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, articleId);
        res = pstmt.executeQuery();

        //Extract results from result set
        HashMap<String, ArticleWord> wordHashMap = new HashMap<>();
        while (res.next()) {
            int wordId = res.getInt("word_id");
            String value  = res.getString("value");
            int wordOffset  = res.getInt("word_offset");
            int parNum  = res.getInt("par_num");
            int parOffset  = res.getInt("par_offset");

            wordHashMap.putIfAbsent(value, new ArticleWord(value));
            ArticleWord word = wordHashMap.get(value);
            word.article.id = articleId;
            word.id = wordId;
            word.wordLocations.add(new WordLocation(wordOffset, parNum, parOffset));
        }

        //Close resources
        pstmt.close();
        res.close();

        return new ArrayList<>(wordHashMap.values());
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

//    public static Article getArticleWithMaxFrequency(Word word){
//        String sql = "SELECT article_id " +
//                "From word_index " +
//                "WHERE "
//    }
}
