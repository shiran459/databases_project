package app;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HtmlParser {
    public static final int CONTEXT_SIZE = 5;
    //=========================== METHODS ===========================//

//        public String clean(String htmlPage){
//
//    }

    /**
     * Creates an index for all the words in a given text. The index holds
     * the locations of each of the words in the text.
     *
     * @param text A text to be indexed.
     * @return A HashMap index of the words.
     */
    public static HashMap<String, ArticleWord> createIndexByOffset(String text) {
        HashMap<String, ArticleWord> index = new HashMap<>();

        //Remove non-alphanumeric characters then split to words
        String[] words = getWordList(text);
        String[] dirtyWords = text.split(" ");

        //Add the current word location to the index
        for (int i = 0; i < words.length; i++) {
            if (index.containsKey(words[i])) {
                index.get(words[i]).offests.add(i);
            } else {
                ArticleWord word = new ArticleWord(words[i]);
                word.offests.add(i);
                index.put(words[i], word);
            }
            index.get(words[i]).contextList.add(getWordContext(dirtyWords, i));
        }

        return index;
    }

    private static String getWordContext(String[] articleText, int location) {
        String context = articleText[location];
        for (int i = 1; i <= CONTEXT_SIZE; i++) {
            if (location - i >= 0)
                context = articleText[location] + context;
            if (location + i < articleText.length)
                context = context + articleText[location];
        }

        return context;
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
        text = text.replaceAll("[^a-zA-Z0-9 ]", "");
        text = text.replaceAll(" ( *)", " ");
        text = text.toLowerCase();
        String[] words = text.split(" ");

        return words;
    }

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
