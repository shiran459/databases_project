package app.lib.wordStats;

import app.utils.Word;

public class WordStats {
    Word word;
    int length;
    int articleId;
    int occurrences;

    public WordStats(Word word, int length, int occurrences, int articleId) {
        this.word = word;
        this.length = length;
        this.articleId = articleId;
        this.occurrences = occurrences;
    }

    @Override
    public String toString() {
        return "id: " + word.id + " length: " + length + " occurrences: " + occurrences + " article_id: " + articleId;
    }
}
