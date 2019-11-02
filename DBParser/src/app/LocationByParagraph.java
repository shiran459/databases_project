package app;

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
        List<String> paragraphList = HTMLPageParser.parseIntoParagraphs(htmlFile);
        for (int i = 0; i < paragraphList.size(); i++){
            String[] wordsInParagraph = HTMLPageParser.getWordList(paragraphList.get(i));
            for (int j = 0; j < wordsInParagraph.length; j++){
                String word = wordsInParagraph[j];
                int[] location = new int[2];
                location[0] = i;
                location[1] = j;
                ArticleWord wordObj = wordMap.get(word);
                if(wordObj.paragraphs == null)
                    wordObj.paragraphs = new LocationByParagraph();
                wordObj.paragraphs.locations.add(location);
            }
        }
    }
}
