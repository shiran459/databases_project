package app.lib;

import app.parsers.HtmlParser;
import app.parsers.XMLParser;
import app.utils.ArticleWord;
import app.utils.ConnectionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * This library holds methods used directly by the website controllers.
 */
public class ServerLib {

    //TODO: Belongs to tester. Remove after done testing.
    public static void wipeTable(String tableName) {
        String sql = "DELETE FROM " + tableName;

        try {
            ConnectionManager.getConnection().createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stores an article and all its relevant information in the DB.
     * Updates the 'ARTICLES', 'WORDS', 'WORD_INDEX', 'CATEGORIES' & 'ARTICLES_BY_CATEGORY' tables.
     * It will also create a new file for the article.
     * @param title
     * @param wikitext
     * @return
     * @throws Exception
     */
    public static void uploadArticle(String title, String wikitext)
            throws Exception {

        //Save html to file
        String html = XMLParser.wikiToHtml(wikitext);
        html = "<div>" + html +"</div>";

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(html)));

        Node root = xmlDocument.getChildNodes().item(0);
        NodeList list = root.getChildNodes();
        System.out.println(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeName().equals("div")){
                root.removeChild(node);
            }
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();

        transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
        html = writer.getBuffer().toString();

        String path = getFilePath(title);
        File htmlFile = createHtmlFile(html, path);

        //Insert article
        int articleId = ArticleLib.insertArticle(title, path);

        //Index the article
        HashMap<String, ArticleWord> wordMap = HtmlParser.indexWords(htmlFile);

        //Update Words and Word_Index tables
        List<ArticleWord> wordsList = new ArrayList<>(wordMap.values());
        WordLib.insertWordIndex(articleId, wordsList);

    }


    public static void uploadArticle(String title, Path wikitextPath) throws Exception{
        String wikitext = Files.readString(wikitextPath);
        uploadArticle(title, wikitext);
    }

    public static String getFilePath(String title) throws IOException{
        String folderName = DigestUtils.sha1Hex(title).substring(0,4);
        Path dictPath =  Paths.get(System.getProperty("user.dir"),
                "DBParser",
                "article pages" ,
                folderName);
        Files.createDirectories(dictPath);

        return Paths.get(dictPath.toString(),
                    title + ".html")
                    .normalize().toString();
    }

    //====================================== PRIVATE METHODS ====================================//

    private static File createHtmlFile(String html, String path) throws IOException {
        //Create an html file
        File htmlFile = new File(path);

        //Save html string into the file
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        try {
            out.write(html);
            return htmlFile;
        }
        finally{
            out.close();
        }
    }
}
