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

    String xmlPath;
    String htmlPath;

    File xmlFile;
    File htmlFile;

    String htmlString;
    XMLParser xmlParser;

    public static void main(String[] args) throws Exception {

        // ################################## RUN SOME TESTS #####################################//
//
//        app.Tester tester = new app.Tester();
//        tester.beforeAll();

//        String xmlpath = "C:\\Users\\User\\Documents\\GitHub\\databases_project\\DBParser\\Examples\\sample2.xml";
//        File xmlfile = new File(xmlpath);
//        app.XMLParser parser = new app.XMLParser(xmlfile);
//        Element page = parser.getNextPage();
//        String s = parser.getPageWikitext(page);
//        String html = parser.wikiToHtml(s);
//        System.out.println(html);


        String htmlpath = "C:\\Users\\User\\Documents\\GitHub\\databases_project\\DBParser\\Examples\\sample2.html";
        File htmlfile = new File(htmlpath);
//        app.HTMLPageParser.parseIntoParagraphs(htmlfile);

        LocationByParagraph a = new LocationByParagraph(htmlfile);

        HashMap<String, List<int[]>> index = a.index;

    }



    //============================================= METHODS ==========================================//
    //----------------------------------------- BEFORE METHODS ---------------------------------------//
    public void beforeAll(){
        xmlPath = "C:\\Users\\Gilad\\Desktop\\sample2.xml";
        htmlPath = "C:\\Users\\Gilad\\Desktop\\html sample 2.html";

        File xmlFile = new File(xmlPath);
        File htmlFile = new File(htmlPath);

        try{
            htmlString =  new String(Files.readAllBytes(Paths.get(htmlPath)));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void beforeTestWordIndex(){
        ServerLib.wipeTable("word_index");
    }

    public void beforeXMLParser(){
        xmlParser = new XMLParser(xmlFile);
    }



    //----------------------------------------- TESTING METHODS -------------------------------------//
    //################################### TEST INSERT METHODS ######################################//
    //This function tests the xml parser
    private void testConstructor(File xmlFile) {
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

    private void printWordIndex(HashMap<String, ArrayList<Integer>> index) {
        for (HashMap.Entry<String, ArrayList<Integer>> indexEntry : index.entrySet()) {
            System.out.println("\n" + indexEntry.getKey() + ":");
            for (Integer location : indexEntry.getValue()) {
                System.out.println(location);
            }
        }
    }
    private void testInsertWordIndex(){
        beforeTestWordIndex();
        HashMap<String,ArrayList<Integer>> index = HTMLPageParser.createIndexByOffset(htmlString);
        testInsertWordIndex(index);
    }

    private void testInsertWordIndex(HashMap<String, ArrayList<Integer>> index) {
        try{
            WordLib.insertWordIndex(1,index);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void testGetTextContent(){
        System.out.println("Text content:\n " + xmlParser.getTextContent());
    }

    private void testCreateIndex(){
        XMLParser parser = new XMLParser(htmlFile);
        String text = parser.getTextContent();
        HashMap<String, ArrayList<Integer>> index = HTMLPageParser.createIndexByOffset(text);

        //Print index
        printWordIndex(index);
    }

    private void testCreateWordGroup(){
        try{
            GroupLib.insertGroup("My Group Name", 1);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void testInsertUser(){
        try{
            UserLib.insertUser("My Usename", "<no hash>", "<no token>", null);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void testInsertArticle(){
        try{
            ArticleLib.insertArticle("Mt title", "<no path>");
        } catch (Exception e){
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

    //######################################### QUERY METHODS ######################################//
    private boolean testGetWordId(String word, int expectedId){
        try{
            return WordLib.getWordId(word) == expectedId;
        }
        catch (SQLException e){}
        return false;
    }

    private boolean testGetArticlePath(int articleId, String expectedPath){
        try{
            return ArticleLib.getArticlePath(articleId).equals(expectedPath);
        }
        catch (SQLException e){}
        return false;
    }

}
