package app.lib;

import app.lib.wordStats.WordStatLib;
import app.lib.wordStats.WordStats;
import app.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        List<WordLocation> locations;

        //Create a statement SQL pattern
        for (ArticleWord word : wordsList) {
            locations = word.wordLocations;

            for (WordLocation location : locations) {
                sql.append("INTO word_index(article_id, word_id, word_offset, par_num, par_offset, word_context) VALUES (?,?,?,?,?,?) ");
            }
        }
        sql.append("SELECT 1 FROM dual");
        //Create a statement object
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql.toString());

        //Fill the SQL pattern with variables
        int i = 0;
        int columns = 6;   //total number of columns in the word index table
        for (ArticleWord word : wordsList) {
            String currWord = word.value;

            boolean success = insertWord(currWord);
            if(!success)
                continue;

            int wordId = getWordId(currWord);
            for (int j = 0; j < word.wordLocations.size(); j++) {
                int offset = word.wordLocations.get(j).wordOffset;
                int paragraph = word.wordLocations.get(j).paragraphNum;
                int paragraphOffset = word.wordLocations.get(j).paragraphOffset;
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

        //close resources
        pstmt.close();

    }

    /**
     * Inserts a word into the word table only if the word does not exist already.
     *
     * @param value String of the word to be added.
     * @return True if the word was added, otherwise returns false.
     */
    public static boolean insertWord(String value) throws SQLException {
        int length = value.length();
        if (length > 256)
            return false;
        String sql = "MERGE INTO words " +
                "USING (SELECT ? word_id,? value FROM dual) new_word " +
                "ON (words.value = new_word.value) " +
                "WHEN NOT MATCHED THEN INSERT (word_id, value) " +
                "VALUES (NULL, new_word.value)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setNull(1, java.sql.Types.INTEGER);
        pstmt.setString(2, value);
        pstmt.executeQuery();

        //close resources
        pstmt.close();
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



        int id = res.next() ?  res.getInt("word_id") : -1;

        res.close();
        pstmt.close();

        return id;
    }

    public static Word getWordById(int wordId) throws SQLException {
        String sql = "SELECT value, word_id " +
                "FROM words " +
                "WHERE word_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        ResultSet res = pstmt.executeQuery();

        Word result;
        if (!res.next()) {
            result =  null;
        } else {
            String value = res.getString("value");
            int id = res.getInt("word_id");

            result = new Word(value, id);
        }

        res.close();
        pstmt.close();

        return result;
    }

    public static List<ArticleWord> searchWordByWordOffset(int wordOffset) throws SQLException{
        String sql = "SELECT word_id,value,article_id,title " +
                "FROM words NATURAL JOIN word_index NATURAL JOIN articles " +
                "WHERE word_offset = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordOffset);
        ResultSet res = pstmt.executeQuery();

        List<ArticleWord> result = new ArrayList<>();
        while (res.next()) {
            int wordId = res.getInt("word_id");
            int articleId = res.getInt("article_id");
            String value = res.getString("value");
            String title = res.getString("title");

            Article article = new Article(articleId, title);
            ArticleWord word = new ArticleWord(value,wordId,article);

            result.add(word);
        }

        res.close();
        pstmt.close();

        return result;
    }

    public static List<ArticleWord> searchWordByParagraph(int parNum, int parOffset) throws SQLException{
        String sql = "SELECT word_id,value,article_id,title " +
                "FROM words NATURAL JOIN word_index NATURAL JOIN articles " +
                "WHERE par_num = ? AND par_offset=?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, parNum);
        pstmt.setInt(2, parOffset);
        ResultSet res = pstmt.executeQuery();

        List<ArticleWord> result = new ArrayList<>();
        while (res.next()) {
            int wordId = res.getInt("word_id");
            int articleId = res.getInt("article_id");
            String value = res.getString("value");
            String title = res.getString("title");

            Article article = new Article(articleId, title);
            ArticleWord word = new ArticleWord(value,wordId,article);

            result.add(word);
        }

        res.close();
        pstmt.close();

        return result;
    }

    public static List<Word> getAllWords() throws SQLException {
        ResultSet res = null;
        String sql = "SELECT DISTINCT word_id, value " +
                "FROM word_index NATURAL JOIN words";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        res = pstmt.executeQuery();

        //Extract results from result set
        List<Word> result = new ArrayList<>();
        while (res.next()) {
            int wordId = res.getInt("word_id");
            String value = res.getString("value");
            Word word = new Word(value, wordId);
            result.add(word);
        }

        //Close resources
        pstmt.close();
        res.close();

        return result;
    }

    public static void setStats(List<Word> words) throws Exception{
        for(Word word : words)
            word.stats = WordStatLib.calculateStats(word);
    }

    /**
     * Searches for all locations of a word in a given article.
     *
     * @param wordId    ArticleWord to be searched.
     * @param articleId Article to be searched in.
     * @return A list of all locations (by offset)
     * @throws SQLException If a transaction has failed.
     */
    static List<Integer> searchWordLocationsByArticle(int wordId, int articleId) throws SQLException {
        String sql = "SELECT word_offset " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        pstmt.setInt(2, articleId);
        ResultSet res = pstmt.executeQuery();

        ArrayList<Integer> locations = new ArrayList<>();
        while (res.next()) {
            locations.add(res.getInt("word_offset"));
        }

        res.close();
        pstmt.close();

        return locations;
    }


    public static List<ArticleWord> getAllContexts(int wordId) throws SQLException {
        String sql = "SELECT article_id, word_context " +
                "FROM word_index NATURAL JOIN articles " +
                "WHERE word_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        ResultSet res = pstmt.executeQuery();

        HashMap<Integer, ArticleWord> wordHashMap = new HashMap<>();

        while (res.next()) {
           int articleId = res.getInt("article_id");
           String context = res.getString("word_context");
           wordHashMap.putIfAbsent(articleId, new ArticleWord());
           ArticleWord articleWord = wordHashMap.get(articleId);

           articleWord.contextList.add(context);
           articleWord.article.id = articleId;
           articleWord.id = wordId;
        }
        //Close resources
        pstmt.close();
        res.close();

        return new ArrayList<>(wordHashMap.values());
    }

    public static List<String>  getContextsInArticle(int wordId, int articleId) throws SQLException {
        String sql = "SELECT word_context " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        pstmt.setInt(2, articleId);
        ResultSet res = pstmt.executeQuery();

        //Extract results from result set
        List<String> result= new ArrayList<>();

        while (res.next()) {
            result.add(res.getString("word_context"));
        }

        //Close resources
        pstmt.close();
        res.close();

        return result;
    }

    /**
     * Returns all words appear in the list of strings that also appear in the DB.
     * @param wordsStrings A list of words to search in the DB.
     * @return A list of the words which exit in the DB.
     * @throws SQLException
     */
    public static List<Word> getWordsFromStrings(List<String> wordsStrings) throws SQLException {
        // Build query
        StringBuilder sqlConstraint = new StringBuilder();
        for(int i=0; i<wordsStrings.size(); i++){
            if(i==wordsStrings.size()-1){
                sqlConstraint.append(" value=?");
            }
            else{
                sqlConstraint.append(" value=? OR");
            }

        }
        String sql = "SELECT word_id, value " +
                "FROM words WHERE"+sqlConstraint;

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        for(int i=0; i<wordsStrings.size();i++){
            pstmt.setString(i+1,wordsStrings.get(i));
        }

        ResultSet res = pstmt.executeQuery();

        // Extract results
        List<Word> wordList = new ArrayList<>();
        while(res.next()){
            int wordId = res.getInt("word_id");
            String value = res.getString("value");

            Word word = new Word(value, wordId);
            wordList.add(word);
        }

        //Close resources
        pstmt.close();
        res.close();

        return wordList;
    }
}
