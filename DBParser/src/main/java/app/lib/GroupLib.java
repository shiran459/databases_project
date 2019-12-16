package app.lib;

import app.utils.ConnectionManager;
import app.utils.Word;
import app.utils.WordGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupLib {
    public static Integer getGroupIdByName(int userId, String groupName) throws SQLException{
        String sql = "SELECT group_id " +
                "FROM groups " +
                "WHERE group_name = ? AND user_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, groupName);
        pstmt.setInt(1, userId);

        ResultSet res = pstmt.executeQuery();

        Integer result;
        if (!res.next()) {
            result =  null;
        } else {
            result = res.getInt("group_id");
        }

        res.close();
        pstmt.close();

        return result;
    }


    /**
     * Inserts a group into the GROUPS table.
     *
     * @param groupName The word group's name.
     * @param userID    User id of the group's creator.
     * @throws SQLException If the transaction has failed.
     */
    public static WordGroup createGroup(String groupName, int userID) throws SQLException {
        //Check if group already exists -> return null
        if(getGroupIdByName(userID, groupName) == null);
            return null;

        //Create a new Group
        java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());
        String sql = "INSERT INTO groups(group_id, group_name, user_id, creation_date) " +
                "VALUES (NULL, ?, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql, new String[]{"group_id"});
        pstmt.setString(1, groupName);
        pstmt.setInt(2, userID);
        pstmt.setDate(3, creationDate);
        pstmt.executeUpdate();

        //Get generated group ID
        ResultSet rs = pstmt.getGeneratedKeys();
        int groupId = -1;
        if (rs.next()) {
            groupId = rs.getInt(1);
        }

        WordGroup group = new WordGroup(groupId, userID, groupName);

        pstmt.close();
        rs.close();

        return group;
    }

    /**
     * Inserts words into a group. Also updates the WORDS_BY_GROUP table.
     *
     * @throws SQLException
     */
    private static void insertWordsToGroup(WordGroup group, Set<Word> wordsToAdd) throws SQLException {
        for (Word word : wordsToAdd) {
            String sql = "MERGE INTO group_words " +
                    "USING (SELECT ? group_id,? word_id FROM DUAL) new_word " +
                    "ON (group_words.word_id = new_word.word_id) " +
                    "WHEN NOT MATCHED THEN INSERT (group_id, word_id) " +
                    "VALUES (NULL, new_word.group_id, new_word.word_id)";
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, group.groupId);
            pstmt.setInt(2, word.id);
            pstmt.executeUpdate();

            pstmt.close();

            group.addWords(wordsToAdd);
        }
    }

    public static Set<Word> getGroupWords(int groupId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT word_id, value " +
                "FROM group_words NATURAL JOIN words " +
                "WHERE group_id = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, groupId);
        res = pstmt.executeQuery();

        Set<Word> result = new HashSet<>();

        while (res.next()) {
            int wordId = res.getInt("word_id");
            String value = res.getString("value");
            result.add(new Word(value, wordId));
        }

        res.close();
        pstmt.close();

        return result;
    }

    public static Set<WordGroup> getGroupsByUser(int userId) throws SQLException {
        ResultSet res = null;
        String sql = "SELECT group_id, user_id, group_name " +
                "FROM groups " +
                "WHERE user_id = ?";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, userId);
        res = pstmt.executeQuery();

        Set<WordGroup> result = new HashSet<>();

        while(res.next()){
            int groupId = res.getInt("group_id");
            String groupName = res.getString("group_name");
            WordGroup group = new WordGroup(groupId,userId,groupName);
            group.words = getGroupWords(groupId);
            result.add(group);
        }

        res.close();
        pstmt.close();

        return result;
    }
}
