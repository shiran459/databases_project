package app;

import java.util.List;
import java.util.ArrayList;

public class ArticleWord {

    public String value;
    public int id;
    public int articleId;
    public List<Integer> offests = new ArrayList<>();
    public LocationByParagraph paragraphs = new LocationByParagraph();
    public List<String> contextList = new ArrayList<>();

    public ArticleWord(String value) {
        this.value = value;
    }

}