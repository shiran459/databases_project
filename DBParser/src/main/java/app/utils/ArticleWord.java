package app.utils;

import java.util.List;
import java.util.ArrayList;

public class ArticleWord {

    public String value;
    public int id;
    public int articleId;
    @Deprecated
    public List<Integer> offests = new ArrayList<>();
    @Deprecated
    public LocationByParagraph paragraphs = new LocationByParagraph();

    public List<String> contextList = new ArrayList<>();
    public List<WordLocation> wordLocations = new ArrayList<>();

    public ArticleWord(String value) {
        this.value = value;
    }

    public String locationsToString(){
        String res = "( ";
        for(WordLocation location : wordLocations)
            res =  res + location + ", ";
        res += " )";
        return res;
    }
}