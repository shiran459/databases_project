package app.lib;

import app.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class GroupLib {
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
            int currWordId = WordLib.getWordId(word);
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
}
