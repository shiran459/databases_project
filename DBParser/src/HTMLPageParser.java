import info.bliki.wiki.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLPageParser {
    //=========================== METHODS ===========================//

//        public String clean(String htmlPage){
//
//    }

        /**
         * Creates an index for all the words in a given text. The index holds
         * the locations of each of the words in the text.
         * @param text
         * @return A HashMap index of the words
         */
        public static HashMap<String,ArrayList<Integer>> createIndex(String text){
            HashMap<String,ArrayList<Integer>> index = new HashMap<>();

            //Remove non-alphanumeric characters then split to words
            text = text.replaceAll("[^a-zA-Z0-9 ]", "");
            text = text.replaceAll(" ( *)", " ");
            text = text.toLowerCase();
            String[] words = text.split(" ");

            //Add the current word location to the index
            for (int i=0; i<words.length; i++){
                if (index.containsKey(words[i])){
                    index.get(words[i]).add(i);
                }
                else{
                    ArrayList<Integer> locations = new ArrayList<Integer>();
                    locations.add(i);
                    index.put(words[i],locations);
                }
            }

            return index;
        }
}
