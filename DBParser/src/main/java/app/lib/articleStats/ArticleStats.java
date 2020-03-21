package app.lib.articleStats;

import app.utils.Article;
import app.utils.Word;

public class ArticleStats {
    public Article article;
    public int parNum;
    public int wordCount;
    public Word commonWord;

    public ArticleStats(Article article, int parNum, int wordCount, Word commonWord) {
        this.article = article;
        this.parNum = parNum;
        this.wordCount = wordCount;
        this.commonWord = commonWord;
    }

    @Override
    public String toString() {
        return "id: " + article.id + " parNum: " + parNum + " wordCount: " + wordCount + " commonWord: " + commonWord.value;
    }
}
