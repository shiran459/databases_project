package app.lib;

import app.ConnectionManager;
import info.bliki.wiki.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.io.File;

public class ServerLib {

    public static void wipeTable(String tableName) {
        String sql = "DELETE FROM " + tableName;

        try {
            ConnectionManager.getConnection().createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean uploadArticle(String title, String wikitextFilePath) {
        //Read file into string
        String wikitext;
        try {
            wikitext = new String(Files.readAllBytes(Paths.get(wikitextFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Convert wikitext string to html
        String html = WikiModel.toHtml(wikitext);

        //Extract categories, word index
        //TODO: Add text analysis here (getCategories(),getWordIndex(),clean the html)

        //Create html file
        String path = "C:\\Users\\Gilad\\Desktop\\" + title + ".html";
        if (!createHtmlFile(html, path)) return false;

        //Update database
        try {
            ArticleLib.insertArticle(title, path);
        } catch (SQLException e) {
            e.printStackTrace();        //TODO: Handle failure due to constraint violation (e.g. non-unique title)
            return false;
        }
        return true;
    }

    //====================================== PRIVATE METHODS ====================================//
    //@TODO: Add a second location


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
