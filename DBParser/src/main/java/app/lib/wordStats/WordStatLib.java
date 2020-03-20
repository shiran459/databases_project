package app.lib.wordStats;

import app.utils.ConnectionManager;
import app.utils.Word;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WordStatLib {

    public static WordStats calculateStats(Word word) throws Exception {
        int length = calculateLength(word);
        int occurrences = calculateOccurrences(word);
        int articleId = calculateArticle(word);

        return new WordStats(word, length, occurrences, articleId);
    }

    private static int calculateLength(Word word) {
        return word.value.length();
    }

    private static int calculateOccurrences(Word word) throws Exception {
        String sql = "SELECT COUNT(*) as occurrences " +
                "FROM word_index " +
                "WHERE word_id=?";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, word.id);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        int occurrences = 0;
        if (res.next()) {
            occurrences = res.getInt("occurrences");
        }

        //Close resources
        pstmt.close();
        res.close();

        return occurrences;
    }

    private static int calculateArticle(Word word) throws Exception {
        String sql =
                "SELECT COUNT(*) as c, article_id " +
                "FROM word_index " +
                "WHERE word_id=? " +
                "GROUP BY word_id, article_id " +
                "ORDER BY c DESC";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, word.id);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        int article = 0;
        if (res.next()) {
            article = res.getInt("article_id");
        }

        //Close resources
        pstmt.close();
        res.close();

        return article;
    }

}
