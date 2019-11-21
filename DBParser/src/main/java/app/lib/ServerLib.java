package app.lib;

import app.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
        String path = "C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\Article Pages\\" + title + ".html";
        File htmlFile = createHtmlFile(html, path);

        String articleText = HtmlParser.getTextContent(htmlFile);

        //Insert article
        int articleId = ArticleLib.insertArticle(title, path);

        //Index the article
        HashMap<String, ArticleWord> wordMap = HtmlParser.createIndexByOffset(articleText); //by offset
        LocationByParagraph.enrichLocationByParagraph(htmlFile, wordMap); //add indexing by paragraph

        //Update Words and Word_Index tables
        List<ArticleWord> wordsList = new ArrayList<>(wordMap.values());
        WordLib.insertWordIndex(articleId, wordsList);
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
