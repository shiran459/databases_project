package app.lib.groupStats;

import app.utils.Word;
import app.utils.WordGroup;

import java.util.Date;

public class GroupStats {
    WordGroup group;
    Date createDate;
    int numOfWords;
    Word longestWord;

    public GroupStats(WordGroup group, Date createDate, int numOfWords, Word longestWord) {
        this.group = group;
        this.createDate = createDate;
        this.numOfWords = numOfWords;
        this.longestWord = longestWord;
    }

    @Override
    public String toString() {
        return "id: " + group.groupId + " createDate: " + createDate + " numOfWords: " + numOfWords + " longestWord: " + longestWord.value;
    }
}
