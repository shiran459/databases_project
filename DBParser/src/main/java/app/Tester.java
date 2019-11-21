package app;

import app.lib.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tester {
    String xmlPath = null;
    String htmlPath = null;

    File xmlFile = null;
    File htmlFile = null;

    String wikitext = null;
    String htmlString = null;

    XMLParser xmlParser = null;


    public static void main(String[] args) throws Exception {
        Tester tester = new Tester();
        tester.beforeAll();

        testUploadArticle(tester.wikitext);
    }


    //============================================= METHODS ==========================================//
    //----------------------------------------- BEFORE METHODS ---------------------------------------//
    public void beforeAll(){
        //Initialize variables
        xmlPath = "C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\Samples\\sample2.xml";
        htmlPath = "C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\Samples\\sample2.html";
        File xmlFile = new File(xmlPath);
        File htmlFile = new File(htmlPath);

        try{
            XMLParser xmlParser= new XMLParser(xmlFile);
            wikitext = xmlParser.getPageWikitext(xmlParser.getNextPage());
            htmlString =  new String(Files.readAllBytes(Paths.get(htmlPath)));
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    //----------------------------------------- TESTING METHODS -------------------------------------//
    //This function tests the xml parser
    private void testConstructor(File xmlFile) {
        xmlParser = new XMLParser(xmlFile);
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //######################################### TEST WORD LIB ######################################//
    private boolean testGetWordId(String word, int expectedId){
        try{
            return WordLib.getWordId(word) == expectedId;
        }
        catch (SQLException e){}
        return false;
    }

    private void printWordIndex(HashMap<String, ArrayList<Integer>> index) {
        for (HashMap.Entry<String, ArrayList<Integer>> indexEntry : index.entrySet()) {
            System.out.println("\n" + indexEntry.getKey() + ":");
            for (Integer location : indexEntry.getValue()) {
                System.out.println(location);
            }
        }
    }


    //####################################### TEST USER LIB ####################################//

    private void testInsertUser(){
        try{
            UserLib.insertUser("My Usename", "<no hash>", "<no token>", null);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //####################################### TEST GROUP LIB ####################################//

    private void testCreateWordGroup(){
        try{
            GroupLib.insertGroup("My Group Name", 1);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void testInsertGroup(){
        try{
            GroupLib.insertGroup("My Group", 5);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //####################################### TEST ARTICLE LIB ####################################//
    private void testInsertArticle(){
        try{
            ArticleLib.insertArticle("Mt title", "<no path>");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean testGetArticlePath(int articleId, String expectedPath){
        try{
            return ArticleLib.getArticlePath(articleId).equals(expectedPath);
        }
        catch (SQLException e){}
        return false;
    }

    //####################################### TEST SERVER LIB ####################################//
    public static void testUploadArticle(String wikitext){
        ServerLib.wipeTable("Word_Index");
        ServerLib.wipeTable("Articles");
        ServerLib.wipeTable("Words");
        try {
            ServerLib.uploadArticle("Some_Title", wikitext);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //##################################### TEST HTML PAGE PARSER ####################################//

    private void testInsertWordIndex(HashMap<String, ArticleWord> index) {
        ServerLib.wipeTable("word_index");
        try{
            List<ArticleWord> wordsList = new ArrayList<>(index.values());
            WordLib.insertWordIndex(1,wordsList);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void testGetTextContent(){
        System.out.println("Text content:\n " + xmlParser.getTextContent());
    }

    private void testCreateIndexByOffset(){
        XMLParser parser = new XMLParser(htmlFile);
        String text = parser.getTextContent();
        HashMap<String,ArticleWord> index = HtmlParser.createIndexByOffset(text);

        //Print index
//        printWordIndex(index);
    }


}
