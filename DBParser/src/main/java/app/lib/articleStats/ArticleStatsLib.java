package app.lib.articleStats;

import app.lib.WordLib;
import app.utils.Article;
import app.utils.ConnectionManager;
import app.utils.Word;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ArticleStatsLib {

    public static ArticleStats calculateArticleStats(Article article) throws Exception {
        int parNum = calculateParNum(article);
        int numOfWords = calculateNumOfWords(article);
        Word commonWord = calculateCommonWord(article);

        return new ArticleStats(article, parNum, numOfWords, commonWord);
    }

    private static int calculateParNum(Article article) throws Exception{
        String sql =
                "SELECT MAX(par_num) as parNum " +
                "FROM word_index " +
                "WHERE article_id=? ";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, article.id);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        int parNum = 0;
        if (res.next()) {
            parNum = res.getInt("parNum");
        }

        //Close resources
        pstmt.close();
        res.close();

        return parNum;
    }

    private static int calculateNumOfWords(Article article) throws Exception{
        String sql =
                "SELECT count(*) " +
                "FROM ( " +
                    "SELECT DISTINCT word_id " +
                    "FROM word_index " +
                    "WHERE article_id=?)";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, article.id);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        int numOfWords = 0;
        if (res.next()) {
            numOfWords = res.getInt(1);
        }

        //Close resources
        pstmt.close();
        res.close();

        return numOfWords;
    }

    private static Word calculateCommonWord(Article article) throws Exception{
        String sql =
                "SELECT COUNT(*) as c, word_id " +
                "FROM word_index " +
                "WHERE article_id=? " +
                "GROUP BY word_id " +
                "ORDER BY c DESC";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, article.id);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        int wordId = -1;
        if (res.next()) {
            wordId = res.getInt("word_id");
        }

        //Close resources
        pstmt.close();
        res.close();

        if(wordId >= 0)
            return WordLib.getWordById(wordId);
        else
            return null;
    }

}
