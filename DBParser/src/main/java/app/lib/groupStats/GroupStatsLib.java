package app.lib.groupStats;

import app.utils.ConnectionManager;
import app.utils.Word;
import app.utils.WordGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class GroupStatsLib {

    public static GroupStats calculateGroupStats(WordGroup group) throws Exception {
        Date createDate = calculateCreateDate(group);
        int numOfWords = calculateNumOfWords(group);
        Word longestWord = calculateLongestWord(group);

        return new GroupStats(group, createDate, numOfWords, longestWord);
    }

    private static Word calculateLongestWord(WordGroup group) {
        Word longestWord = null;
        for (Word word : group.words) {
            if (longestWord == null)
                longestWord = word;
            else if (word.value.length() > longestWord.value.length())
                longestWord = word;
        }

        return longestWord;
    }

    private static int calculateNumOfWords(WordGroup group) {
        return group.words.size();
    }

    private static Date calculateCreateDate(WordGroup group) throws Exception {
        String sql =
                "SELECT creation_date " +
                        "FROM groups " +
                        "WHERE group_id = ? ";

        // Set query values
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, group.groupId);
        ResultSet res = pstmt.executeQuery();

        // Extract results
        Date date = null;
        if (res.next()) {
            date = res.getDate(1);
        }

        //Close resources
        pstmt.close();
        res.close();

        return date;
    }
}
