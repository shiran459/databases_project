package app.utils;

import app.lib.articleStats.ArticleStats;

public class Article {
    public int id;
    public String title;
    public String path;
    public ArticleStats stats;

    public Article(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Article() {
        this.id = -1;
    }
}
