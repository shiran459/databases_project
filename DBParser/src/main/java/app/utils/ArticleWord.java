package app.utils;

import java.util.List;
import java.util.ArrayList;

public class ArticleWord extends Word{

    public int articleId;

    public List<String> contextList = new ArrayList<>();
    public List<WordLocation> wordLocations = new ArrayList<>();

    public ArticleWord(String value) {
        super(value);
    }

    public ArticleWord(String value, int id) {
       super(value, id);
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