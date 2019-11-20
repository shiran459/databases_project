package app.lib;

import app.ArticleWord;
import app.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WordLib {
    /**
     * Stores the words of a word index at the WORDS and WORD_INDEX tables.
     *
     * @param articleId The article the index belongs to.
     * @param wordsList The index to be stored.
     * @throws SQLException If the transaction has failed.
     */
    public static void insertWordIndex(int articleId, List<ArticleWord> wordsList) throws SQLException {
        //Prepare an SQL statement
        StringBuilder sql = new StringBuilder("INSERT ALL ");

        List<Integer> locations;

        //Create a statement SQL pattern
        for (ArticleWord word : wordsList) {
            locations = word.offests;

            for (int location : locations) {
                sql.append("INTO word_index(article_id, word_id, word_offset, par_num, par_offset, word_context) VALUES (?,?,?,?,?,?) ");
            }
        }
        sql.append("SELECT 1 FROM dual");
        System.out.println(sql);

        //Create a statement object
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString());

            //Fill the SQL pattern with variables
            int i = 0;
            int columns = 6;   //total number of columns in the word index table
            for (ArticleWord word : wordsList) {
                String currWord = word.value;

                insertWord(currWord);
                int wordId = getWordId(currWord);
                for (int j = 0; j < word.offests.size(); j++) {
                    int offset = word.offests.get(j);
                    int paragraph = word.paragraphs.getParagraph(j);
                    int paragraphOffset = word.paragraphs.getParagraphOffset(j);
                    String context = word.contextList.get(j);

                    pstmt.setInt(columns * i + 1, articleId);
                    pstmt.setInt(columns * i + 2, wordId);
                    pstmt.setInt(columns * i + 3, offset);
                    pstmt.setInt(columns * i + 4, paragraph);
                    pstmt.setInt(columns * i + 5, paragraphOffset);
                    pstmt.setString(columns * i + 6, context);

                    i++;
                }
            }

            //Execute the statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Inserts a word into the word table only if the word does not exist already.
     *
     * @param value String of the word to be added.
     * @return True if the word was added, otherwise returns false.
     */
    public static boolean insertWord(String value) {
        int length = value.length();
        String sql = "MERGE INTO words " +
                "USING (SELECT ? word_id,? value,? length FROM dual) new_word " +
                "ON (words.value = new_word.value) " +
                "WHEN NOT MATCHED THEN INSERT (word_id, value, length) " +
                "VALUES (NULL, new_word.value, new_word.length)";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setNull(1, java.sql.Types.INTEGER);
            pstmt.setString(2, value);
            pstmt.setInt(3, length);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //--------------------------------------- QUERY METHODS -------------------------------//
    public static int getWordId(String word) throws SQLException {
        String sql = "SELECT word_id " +
                "FROM words " +
                "WHERE value = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, word);
        ResultSet res = pstmt.executeQuery();

        if (!res.next()) {
            return -1;
        } else {
            return res.getInt("word_id");
        }
    }

    public static String getWordValue(int wordId) throws SQLException {
        String sql = "SELECT value " +
                "FROM words " +
                "WHERE word_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        ResultSet res = pstmt.executeQuery();

        if (!res.next()) {
            return null;
        } else {
            return res.getString("value");
        }
    }

    public static List<String> getAllWords() throws SQLException {
        ResultSet res = null;
        String sql = "SELECT value " +
                "FROM word_index NATURAL JOIN words";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
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

    /**
     * Searches for all locations of a word in a given article.
     *
     * @param wordId    ArticleWord to be searched.
     * @param articleId Article to be searched in.
     * @return A list of all locations (by offset)
     * @throws SQLException If a transaction has failed.
     */
    static List<Integer> searchWordLocationsInArticle(int wordId, int articleId) throws SQLException {
        String sql = "SELECT offset " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        pstmt.setInt(2, articleId);
        ResultSet res = pstmt.executeQuery();

        ArrayList<Integer> locations = new ArrayList<>();
        while (res.next()) {
            locations.add(res.getInt("offset"));
        }

        return locations;
    }

    public static Object[] getContexts(int wordId) throws SQLException {
        String sql = "SELECT article_id, title, context " +
                "FROM word_index NATURAL JOIN articles " +
                "WHERE word_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        ResultSet res = pstmt.executeQuery();
        pstmt.setInt(1, wordId);

        //Extract results from result set
        List<Integer> articleIdList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<String> contextList = new ArrayList<>();
        Object[] result = {articleIdList, titleList, contextList};

        while (res.next()) {
            articleIdList.add(res.getInt("article_id"));
            titleList.add(res.getString("title"));
            contextList.add(res.getString("context"));
        }

        //Close resources
        pstmt.close();
        res.close();

        return result;
    }

}
