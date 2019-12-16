package app;

import app.lib.*;
import app.parsers.HtmlParser;
import app.parsers.XMLParser;
import app.utils.ArticleWord;
import app.utils.User;

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

        tester.
        testCreateGroup();
    }


    //============================================= METHODS ==========================================//
    //----------------------------------------- BEFORE METHODS ---------------------------------------//
    public void beforeAll(){
        //Initialize variables
        xmlPath = "C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\Samples\\sample.xml";
        htmlPath = "C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\Samples\\sample.html";
        xmlFile = new File(xmlPath);
        htmlFile = new File(htmlPath);

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
    public static void testRegister(){
        ServerLib.wipeTable("users");
        try {
            User user = UserLib.register("shiran", "Password1");
            System.out.println("Token: " + user.token);
            System.out.println("Id: " + user.userId);
            System.out.println("Username: " + user.username);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void testLogin(){
        try {
            User user = UserLib.login("shiran", "Password1");
            System.out.println("Token: " + user.token);
            System.out.println("Id: " + user.userId);
            System.out.println("Username: " + user.username);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void testValidSession(){
        try {
            User user = UserLib.login("shiran", "Password1");
            User user2 = UserLib.currentUser(user.token);
            System.out.println("Id: " + user2.userId);
            System.out.println("Username: " + user2.username);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //####################################### TEST GROUP LIB ####################################//

    public static void testCreateGroup(){
        try{
            GroupLib.createGroup("My Group Name", 1);
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
            return ArticleLib.getArticleById(articleId).equals(expectedPath);
        }
        catch (SQLException e){}
        return false;
    }

    //####################################### TEST SERVER LIB ####################################//
    public static void testUploadArticle(String wikitext){
        ServerLib.wipeTable("Word_Index");
        ServerLib.wipeTable("Articles");
        ServerLib.wipeTable("Words");
//        try {
//            ServerLib.uploadArticle("Some_Title", wikitext);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
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

    private void testWordLocationsExtractor() throws Exception{

        HashMap<String, ArticleWord> wordsMap = HtmlParser.indexWords(htmlFile);

        for (String key : wordsMap.keySet()){
            System.out.println(key + " :  " + wordsMap.get(key).locationsToString());
        }
    }
}
