package app.lib;

import app.*;
import info.bliki.wiki.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class ServerLib {

    public static void wipeTable(String tableName) {
        String sql = "DELETE FROM " + tableName;

        try {
            ConnectionManager.getConnection().createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean uploadArticle(String title, String wikitext)
            throws Exception {

        //convert to html
        String htmlContent = "<page>" + XMLParser.wikiToHtml(wikitext) + "</page>";
        //Save html to file
        Path filePath = Paths.get("Articles", title + ".html");
        Files.writeString(filePath, htmlContent, StandardOpenOption.APPEND);
        File htmlFile = new File(filePath.toString());
        XMLParser parser = new XMLParser(htmlFile);

        String articleText = parser.getTextContent();

        //Insert article
        int articleId = ArticleLib.insertArticle(title, filePath.toString());
        //create ArticleWords and offset
        HashMap<String, ArticleWord> wordMap = HTMLPageParser.createIndexByOffset(articleText);
        //Enrich offset by paragraph
        LocationByParagraph.enrichLocationByParagraph(htmlFile, wordMap);
        //Update Word and WordIndex tables
        List<ArticleWord> wordsList = new ArrayList<>(wordMap.values());
        WordLib.insertWordIndex(articleId, wordsList);

        return true;
    }

    //====================================== PRIVATE METHODS ====================================//

    private static boolean createHtmlFile(String html, String path) {
        //Create an html file
        File htmlFile = new File(path);

        try {
            if (!htmlFile.createNewFile()) {   //failed to create a file
                return false;
            }
            //Save html string into the file
            Files.writeString(Paths.get(path), html);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*  //TODO:remove this if necessary
    private static ResultSet searchArticles(String title, String category, ArrayList<String> containsWords){
        //Determine query pattern
        String select, from, where;

        String[] titleQuery = {"Select article_id, title ", "FROM articles ", "WHERE title = ?"};
        String[] categoryQuery = {"SELECT article_id, title ", "FROM articles NATURAL JOIN categories ",
                "WHERE category = ?"};
        String[] containsWordsQuery = {"SELECT article_id, title ", "FROM articles NATURAL JOIN word_index"};


        select = "SELECT article_id, title";
        if (title == null){
            if (category == null){
                if(containsWords == null){
                    from = "FROM articles ";
                    where = "";
                }
                else{
                    from = "FROM articles NATURAL JOIN word_index";
                    where = "WHERE article_id IN";

                    StringBuilder containsWordQuery = new StringBuilder();
                    for (int i=0; i<containsWords.size(); i++) {
                        if
                        containsWordQuery
                    }
                }
            }
        }



        //Create and run query
        try {
            PreparedStatement pstmt = app.ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
*/


    //    private static ArrayList<Integer> getWordLocationsByOffset(int wordId, int articleId)
//    throws SQLException{
//        ResultSet res = null;
//        String sql = "SELECT offset " +
//                "FROM word_index " +
//                "WHERE word_id = ? AND article_id = ?";
//        try {
//            PreparedStatement pstmt = app.ConnectionManager.getConnection().prepareStatement(sql);
//            res = pstmt.executeQuery();
//            pstmt.setInt(1, wordId);
//            pstmt.setInt(2, articleId);
//        } catch (SQLException e) {
//            throw new SQLException();
//        }
//
//        res.next();
//        if(res == null){    //word does not appear in the article
//            return -1;
//        }
//        else{
//            return res.getInt("offset");
//        }
//    }


}
