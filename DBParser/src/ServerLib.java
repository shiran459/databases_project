import info.bliki.wiki.model.*;
import oracle.sql.TIMESTAMP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.io.File;
import java.util.*;

public class ServerLib {

    public static void wipeTable(String tableName) {
        String sql = "DELETE FROM " + tableName;

        try {
            ConnectionManager.getConnection().createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean uploadArticle(String title, String wikitextFilePath) {
        //Read file into string
        String wikitext;
        try {
            wikitext = new String(Files.readAllBytes(Paths.get(wikitextFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Convert wikitext string to html
        String html = WikiModel.toHtml(wikitext);

        //Extract categories, word index
        //TODO: Add text analysis here (getCategories(),getWordIndex(),clean the html)

        //Create html file
        String path = "C:\\Users\\Gilad\\Desktop\\" + title + ".html";
        if (!createHtmlFile(html, path)) return false;

        //Update database
        try {
            insertArticle(title, path);
        } catch (SQLException e) {
            e.printStackTrace();        //TODO: Handle failure due to constraint violation (e.g. non-unique title)
            return false;
        }
        return true;
    }

    //====================================== PRIVATE METHODS ====================================//
    //@TODO: Add a second location

    /**
     * Stores the words of a word index at the WORDS and WORD_INDEX tables.
     *
     * @param articleId The article the index belongs to.
     * @param index     The index to be stored.
     * @throws SQLException If the transaction has failed.
     */
    public static void insertWordIndex(int articleId, HashMap<String, ArrayList<Integer>> index) throws SQLException {
        //Prepare an SQL statement
        StringBuilder sql = new StringBuilder("INSERT ALL ");

        ArrayList<Integer> locations;

        //Create a statement SQL pattern
        for (HashMap.Entry<String, ArrayList<Integer>> entry : index.entrySet()) {
            locations = entry.getValue();

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
            for (HashMap.Entry<String, ArrayList<Integer>> entry : index.entrySet()) {
                String currWord = entry.getKey();

                insertWord(currWord);
                int wordId = getWordId(currWord);
                locations = entry.getValue();

                for (int location : locations) {
                    pstmt.setInt(columns * i + 1, articleId);
                    pstmt.setInt(columns * i + 2, wordId);
                    pstmt.setInt(columns * i + 3, location);
                    pstmt.setInt(columns * i + 4, -1);
                    pstmt.setInt(columns * i + 5, -1);
                    pstmt.setString(columns * i + 6, "<no context>");

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

    /**
     * Inserts a user into the USERS table.
     *
     * @param username            Username chosen by the user.
     * @param passwordHash        Hash for the user's password.
     * @param sessionToken        Token for a user's current website session.
     * @param tokenExpirationTime Expiration time of the current session token.
     * @throws SQLException In case the transaction has failed.
     */
    public static void insertUser(String username, String passwordHash, String sessionToken, Timestamp tokenExpirationTime)
            throws SQLException {
        String sql = "INSERT INTO users(user_id, username, password_hash, session_token, token_expiration_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

            pstmt.setNull(1, java.sql.Types.INTEGER);
            pstmt.setString(2, username);
            pstmt.setString(3, passwordHash);
            pstmt.setString(4, sessionToken);
            pstmt.setTimestamp(5, tokenExpirationTime);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Inserts an article into the ARTICLES table.
     *
     * @param title Title of the article.
     * @param path  Path to the HTML file of the article.
     * @throws SQLException If the transaction has failed.
     */
    public static void insertArticle(String title, String path) throws SQLException {
        String sql = "INSERT INTO articles(article_id, article_name,path)" +
                "VALUES (?, ? ,?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setNull(1, java.sql.Types.INTEGER);
        pstmt.setString(2, title);
        pstmt.setString(3, path);
        pstmt.executeUpdate();
    }


    /**
     * Inserts a group into the GROUPS table.
     *
     * @param groupName The word group's name.
     * @param userID    User id of the group's creator.
     * @throws SQLException If the transaction has failed.
     */
    public static void insertGroup(String groupName, int userID) throws SQLException {
        java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());
        String sql = "INSERT INTO groups(group_id, group_name, user_id, creation_date) " +
                "VALUES (NULL, ?, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, groupName);
        pstmt.setInt(2, userID);
        pstmt.setDate(3, creationDate);
        pstmt.executeUpdate();
    }

    /**
     * Inserts a word set belong to a group into the WORDS_BY_GROUP table.
     *
     * @param groupId ID of the group.
     * @param words   Word set of the group.
     * @throws SQLException
     */
    private static void insertGroupWordSet(int groupId, Set<String> words) throws SQLException {
        for (String word : words) {
            int currWordId = Server.getWordId(word);
            String sql = "MERGE INTO group_words " +
                    "USING (SELECT ? group_id,? word_id FROM DUAL) new_word " +
                    "ON (group_words.word_id = new_word.word_id) " +
                    "WHEN NOT MATCHED THEN INSERT (group_id, word_id) " +
                    "VALUES (NULL, new_word.group_id, new_word.word_id)";
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, currWordId);
            pstmt.executeUpdate();
        }
    }

    public static void insertExpression(int userId, String wordIdList, String value, int wordCount) throws SQLException{
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

    private static boolean createHtmlFile(String html, String path) {
        //Create an html file
        File htmlFile = new File(path);

        try {
            if (!htmlFile.createNewFile()) {   //failed to create a file
                return false;
            }
            //Save html string into the file
            Files.writeString(Paths.get(path), html);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    /*  //TODO:remove this if necessary
    private static ResultSet searchArticles(String title, String category, ArrayList<String> containsWords){
        //Determine query pattern
        String select, from, where;

        String[] titleQuery = {"Select article_id, title ", "FROM articles ", "WHERE title = ?"};
        String[] categoryQuery = {"SELECT article_id, title ", "FROM articles NATURAL JOIN categories ",
                "WHERE category = ?"};
        String[] containsWordsQuery = {"SELECT article_id, title ", "FROM articles NATURAL JOIN word_index"};


        select = "SELECT article_id, title";
        if (title == null){
            if (category == null){
                if(containsWords == null){
                    from = "FROM articles ";
                    where = "";
                }
                else{
                    from = "FROM articles NATURAL JOIN word_index";
                    where = "WHERE article_id IN";

                    StringBuilder containsWordQuery = new StringBuilder();
                    for (int i=0; i<containsWords.size(); i++) {
                        if
                        containsWordQuery
                    }
                }
            }
        }



        //Create and run query
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
*/

    private static ResultSet searchArticlesByTitle(String title) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT article_id, title " +
                "FROM articles " +
                "WHERE title LIKE ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, title);
        res = pstmt.executeQuery();

        return res;
    }

    private static ResultSet searchArticlesByCategory(String category) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT article_id, title " +
                "FROM articles_by_category NATURAL JOIN categories " +
                "WHERE category_name = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, category);
        res = pstmt.executeQuery();

        return res;
    }

    private static ResultSet getArticlesByWord(int wordId) throws SQLException {
        ResultSet res = null;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT DISTINCT article_id " +
                "FROM word_index " +
                "WHERE word_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        res = pstmt.executeQuery();

        return res;
    }

    private static ResultSet searchArticlesByWords(ArrayList<String> containsWords) throws SQLException {
        ResultSet res = null;
        StringBuilder wordSubQueries = new StringBuilder();
        String sql = "SELECT article_id, title " +
                "FROM word_index NATURAL JOIN articles NATURAL JOIN words " +
                "WHERE article_id IN ";
        for (int i = 0; i < containsWords.size(); i++) {
            if (i == containsWords.size() - 1) {
                wordSubQueries.append("(SELECT article_id FROM word_index NATURAL JOIN words WHERE value = ?)");
            } else {
                wordSubQueries.append("(SELECT article_id FROM word_index NATURAL JOIN words WHERE value = ?) INTERSECT ");
            }
        }
        sql += "(" + wordSubQueries + ")";

        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            for (int i = 0; i < containsWords.size(); i++) {
                pstmt.setString(i + 1, containsWords.get(i));
            }
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }

    /**
     * Searches for all locations of a word in a given article.
     * @param wordId Word to be searched.
     * @param articleId Article to be searched in.
     * @return A list of all locations (by offset)
     * @throws SQLException If a transaction has failed.
     */
    private static List<Integer> searchWordLocationsInArticle(int wordId, int articleId) throws SQLException{
        String sql = "SELECT offset " +
                "FROM word_index " +
                "WHERE word_id = ? AND article_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, wordId);
        pstmt.setInt(2, articleId);
        ResultSet res = pstmt.executeQuery();

        ArrayList<Integer> locations = new ArrayList<>();
        while (res.next()){
            locations.add(res.getInt("offset"));
        }

        return locations;
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
    private static List<Integer> searchExpressionInArticle(int expressionId, int aricleId) throws SQLException {
        ArrayList<Integer> expressionWords = getExpressionWordIdList(expressionId);

        List<Integer> potentialExpressionLocations = searchWordLocationsInArticle(expressionWords.get(0), aricleId); //Locations of the first word in the word ID list

        if (potentialExpressionLocations.isEmpty())
            return potentialExpressionLocations;

        for (int i = 1; i < expressionWords.size(); i++) {
            List<Integer> currWordLocations = searchWordLocationsInArticle(expressionWords.get(i), aricleId); //Locations of i'th word

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

    //TODO:Continue writing
//    private static List<Integer> getArticlesByExpression(int expressionId) throws SQLException{
//
//    }

    private static ResultSet getArticleWords(int articleId) throws SQLException {
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


    private static ResultSet getAllWords() throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id, value " +
                "FROM word_index NATURAL JOIN words";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }

    private static ResultSet getContexts(int wordId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT article_id, context " +
                "FROM word_index " +
                "WHERE word_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, wordId);
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
    }

    private static ResultSet getLocationsByOffset(int wordId, int articleId) throws SQLException {
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

    private static ResultSet getLocationsByParagraph(int wordId, int articleId) throws SQLException {
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

    private static ResultSet getWordGroups(int userId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT group_id, group_name " +
                "FROM groups " +
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

    private static ResultSet getGroupWords(int groupId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id, value " +
                "FROM group_words NATURAL JOIN words " +
                "WHERE group_id = ?";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            res = pstmt.executeQuery();
            pstmt.setInt(1, groupId);
        } catch (SQLException e) {
            throw new SQLException();
        }
        return res;
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

    private static ArrayList<Integer> getExpressionWordIdList(int expressionId)
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

//    private static ArrayList<Integer> getWordLocationsByOffset(int wordId, int articleId)
//    throws SQLException{
//        ResultSet res = null;
//        String sql = "SELECT offset " +
//                "FROM word_index " +
//                "WHERE word_id = ? AND article_id = ?";
//        try {
//            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
//            res = pstmt.executeQuery();
//            pstmt.setInt(1, wordId);
//            pstmt.setInt(2, articleId);
//        } catch (SQLException e) {
//            throw new SQLException();
//        }
//
//        res.next();
//        if(res == null){    //word does not appear in the article
//            return -1;
//        }
//        else{
//            return res.getInt("offset");
//        }
//    }


}
