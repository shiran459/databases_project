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

    /**
     * Gets
     * @param parNum
     * @return
     */
    public int getParagraphOffset(int parNum){
        if (parNum >= locations.size())
            return -1;
        return locations.get(parNum)[1];
    }

    /**
     * Adds all the locations by paragraph (paragraph number, word offset inside the paragraph) to an article word.
     * @param htmlFile File of the HTML article
     * @param wordMap
     * @throws Exception
     */
    public static void enrichLocationByParagraph(File htmlFile, HashMap<String, ArticleWord> wordMap) throws Exception{
        //Extract paragraphs text
        List<String> paragraphList = HtmlParser.parseIntoParagraphs(htmlFile);

        //Extract location by paragraph for all words in the text
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
