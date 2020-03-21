package app.utils;

import app.lib.groupStats.GroupStats;

import java.util.HashSet;
import java.util.Set;

public class WordGroup {
    public int groupId;
    public int userId;
    public String groupName;
    public Set<Word> words = new HashSet<>();
    public GroupStats stats;

    public WordGroup(int groupId, int userId, String groupName) {
        this.groupId = groupId;
        this.userId = userId;
        this.groupName = groupName;
    }

    public void addWords(Set<Word> wordsToAdd){
        words.addAll(wordsToAdd);
    }
}
