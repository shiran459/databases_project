package app.parsers;

import app.utils.ArticleWord;
import app.utils.WordLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPath;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HtmlParser {
    public static final int CONTEXT_WORD_SIZE = 5;
    public static final int CONTEXT_CHAR_SIZE = 200;


    public static int wordsCounter = 1; //counter of the total words in an article
    //=========================== METHODS ===========================//

//        public String clean(String htmlPage){
//
//    }

    private static String trimContext(String context){
        int length = Math.min(context.length(), CONTEXT_CHAR_SIZE);
        return context.substring(0, length);
    }

    public static  HashMap<String, ArticleWord> indexWords(File htmlFile) throws Exception {
        //Create XPath object
        FileInputStream fileIS = new FileInputStream(htmlFile);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(fileIS);
        XPath xPath = XPathFactory.newInstance().newXPath();

        //Extract paragraphs
        String expression = "/div/*";
        NodeList paragraphList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        //extract words from each paragraph
        HashMap<String, ArticleWord> wordsMap = new HashMap<>();

        for (int i=0; i < paragraphList.getLength(); i++){
            Node paragraph = paragraphList.item(i);
            extractWordsFromParagraph(paragraph, wordsMap, i+1);
        }

        return wordsMap;
    }

    public static void extractWordsFromParagraph(Node paragraph,  HashMap<String, ArticleWord> wordsMap, int paragraphNumber){
        String textContent = paragraph.getTextContent();
        String[] words = getWordList(textContent);
        int paragraphOffset = 1;
        for (String word: words){
            if (word.isEmpty())
                continue;

            WordLocation location = new WordLocation(wordsCounter, paragraphNumber, paragraphOffset);
            if (wordsMap.containsKey(word))
                wordsMap.get(word).wordLocations.add(location);
            else{
                ArticleWord articleWord = new ArticleWord(word);
                articleWord.wordLocations.add(location);
                wordsMap.put(word,articleWord);
            }
            wordsMap.get(word).contextList.add(getWordContext(words, paragraphOffset-1));
            paragraphOffset++;
            wordsCounter++;
        }
    }

    private static String getWordContext(String[] articleText, int location) {
        String context = articleText[location];
        for (int i = 1; i <= CONTEXT_WORD_SIZE; i++) {
            if (location - i >= 0)
                context = articleText[location - i] + " " + context;
            if (location + i < articleText.length)
                context = context + " " + articleText[location + i];
        }

        return trimContext(context);
    }

    /**
     * Parses a text into a list of words.
     * Non-alphanumeric characters will be removed and all
     * words will be converted to lowercase.
     *
     * @param text String of text to be parsed.
     * @return An array of the text's words.
     */
    public static String[] getWordList(String text) {
        text = text.replaceAll("[^a-zA-Z0-9 ]", " ");
        text = text.replaceAll(" ( *)", " ");
        text = text.toLowerCase();
        String[] words = text.split(" ");

        return words;
    }

    @Deprecated
    public static List<String> parseIntoParagraphs(File htmlFile)
            throws Exception {
        List<String> parsed = new ArrayList<>();

        //Create XPath object
        FileInputStream fileIS = new FileInputStream(htmlFile);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(fileIS);
        XPath xPath = XPathFactory.newInstance().newXPath();

        //Extract paragraphs
        String expression = "//p | //ul";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                parsed.add(el.getTextContent());
            }
        }

        return parsed;
    }

    /**
     * Gets all inner-text of an html document
     *
     * @return String of the inner-text
     */
    public static String getTextContent(File htmlFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(htmlFile);
        doc.getDocumentElement().normalize();

        StringBuilder result = new StringBuilder();
        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                result.append(node.getTextContent());
            }
        }
        return result.toString();
    }
}
