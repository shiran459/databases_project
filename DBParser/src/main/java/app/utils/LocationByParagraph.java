package app.utils;

import app.parsers.HtmlParser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LocationByParagraph {
    public List<int[]> locations = new ArrayList<int[]>();

    public int getParagraph(int parNum){
        if (parNum >= locations.size())
            return -1;
        return locations.get(parNum)[0];
    }

    public int getParagraphOffset(int parNum){
        if (parNum >= locations.size())
            return -1;
        return locations.get(parNum)[1];
    }

    public static void enrichLocationByParagraph(File htmlFile, HashMap<String, ArticleWord> wordMap) throws Exception{
        List<String> paragraphList = HtmlParser.parseIntoParagraphs(htmlFile);
        for (int i = 0; i < paragraphList.size(); i++){
            String[] wordsInParagraph = HtmlParser.getWordList(paragraphList.get(i));
            for (int j = 0; j < wordsInParagraph.length; j++){
                String word = wordsInParagraph[j];
                if (word.isEmpty())
                    continue;
                int[] location = new int[2];
                location[0] = i;
                location[1] = j;
                ArticleWord wordObj = wordMap.get(word);
                wordObj.paragraphs.locations.add(location);
            }
        }
    }
}
