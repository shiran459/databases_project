package app.parsers;

import app.lib.ServerLib;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.HashMap;

public class XMLParser {
    //====================== INSTANCE VARIABLES =====================//
    private int currPageNum = 0;
    private final int MAX_PAGES = 10000;
    private NodeList pages;
    private Document doc;
    private final String ARTICLE_NAMESPACE = "0";

    //========================= CONSTRUCTORS ========================//
    public XMLParser(File xmlFile) {
        try {
            //Create XML DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement();
            pages = doc.getElementsByTagName("page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=========================== METHODS ===========================//

    /**
     * Returns an XML Element of the next Wikipedia page.
     *
     * @return XML Element of the next page.
     */
    public Element getNextPage() {
        Element currPageNode = (Element) pages.item(currPageNum);
        currPageNum++;
        return currPageNode;
    }

    public boolean isArticlePage(Element page) {
        String namespace = page.getElementsByTagName("ns").item(0).getTextContent();
        if (!namespace.equals(ARTICLE_NAMESPACE))
            return false;
        return true;
    }

    public String getPageTitle(Element page) {
        String title = page.getElementsByTagName("title").item(0).getTextContent();
        return title.replaceAll("\\W", " ");
    }

    public String getPageWikitext(Element page) {
        String wikitext = page.getElementsByTagName("text").item(0).getTextContent();

        //Replace entity references with their character (lt: <, gt: >, apos: ', quot: ", amp: &)
        HashMap<String, String> entities = new HashMap<>();
        entities.put("&lt;", "<");
        entities.put("&gt;", ">");
        entities.put("&apos;", "\'");
        entities.put("&quot;", "\"");
        entities.put("&amp;", "&");
        entities.put("\t", "");

        for (HashMap.Entry<String, String> entity : entities.entrySet()) {
            wikitext = wikitext.replaceAll(entity.getKey(), entity.getValue());
        }

        return wikitext;
    }

    /**
     * Gets all inner-text of an XML document
     *
     * @return String of the inner-text
     */
    public String getTextContent() {
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

    public static String wikiToHtml(String wikitext) {

        return info.bliki.wiki.model.WikiModel.toHtml(wikitext);
    }

    public void initDatabase(boolean wet) throws Exception {
        while (currPageNum < MAX_PAGES) {
            Element page = getNextPage();
            if (isArticlePage(page)) {
                String title = getPageTitle(page);
                String content = getPageWikitext(page);
                System.out.println(title);
                if (wet) {
                    ServerLib.uploadArticle(title, content);
                }
            }
        }
    }
}
