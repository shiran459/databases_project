package app.utils;

import java.util.List;
import java.util.ArrayList;

public class ArticleWord extends Word{

    public Article article = new Article();

    public List<String> contextList = new ArrayList<>();
    public List<WordLocation> wordLocations = new ArrayList<>();


    // Constructors
    public ArticleWord(String value) {
        super(value);
    }

    public ArticleWord(String value, int wordId,Article article) {
       super(value, wordId);
       this.article = article;
    }

    public ArticleWord() {
        super();
    }

    public String locationsToString(){
        String res = "( ";
        for(WordLocation location : wordLocations)
            res =  res + location + ", ";
        res += " )";
        return res;
    }

}