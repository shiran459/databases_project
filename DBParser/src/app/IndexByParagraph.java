package app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class IndexByParagraph {
    public HashMap<String, List<int[]>> index = new HashMap<>();

    public IndexByParagraph(File htmlFile) throws Exception{
        List<String> paragraphList = HTMLPageParser.parseIntoParagraphs(htmlFile);
        for (int i = 0; i < paragraphList.size(); i++){
            String[] wordsInParagraph = HTMLPageParser.getWordList(paragraphList.get(i));
            for (int j = 0; j < wordsInParagraph.length; j++){
                String word = wordsInParagraph[j];
                int[] location = new int[2];
                location[0] = i;
                location[1] = j;
                index.putIfAbsent(word, new ArrayList<>());
                index.get(word).add(location);
            }
        }
    }
}
