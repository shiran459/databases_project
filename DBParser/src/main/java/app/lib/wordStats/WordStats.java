package app.lib.wordStats;

import app.utils.Article;
import app.utils.Word;

public class WordStats {
    public Word word;
    public int length;
    public Article article;
    public int occurrences;

    public WordStats(Word word, int length, int occurrences, Article article) {
        this.word = word;
        this.length = length;
        this.article = article;
        this.occurrences = occurrences;
    }

    @Override
    public String toString() {
        return "id: " + word.id + " length: " + length + " occurrences: " + occurrences + " article_id: " + article.id;
    }
}
