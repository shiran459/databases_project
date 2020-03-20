package app;

import app.lib.*;
import app.lib.articleStats.ArticleStats;
import app.lib.articleStats.ArticleStatsLib;
import app.lib.expressionStats.ExpressionStats;
import app.lib.expressionStats.ExpressionStatsLib;
import app.lib.groupStats.GroupStats;
import app.lib.groupStats.GroupStatsLib;
import app.lib.wordStats.WordStatLib;
import app.lib.wordStats.WordStats;
import app.parsers.HtmlParser;
import app.parsers.XMLParser;
import app.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

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

        tester.testExpressionStats();
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

    public  static void testGetWordsFromStrings(List<String> stringList){
        try{
            System.out.println("Words searched: " + stringList);
            List<Word> words = WordLib.getWordsFromStrings(stringList);
            System.out.println("Words found: "+words);
        } catch (Exception e){
            e.printStackTrace();
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

    private void testGetExpressions() throws Exception{
        List<Expression> expressions = ExpressionLib.getExpressions(7);
        for(Expression expression : expressions)
            System.out.println(expression);
    }

    private void testInsetExpressions() throws Exception{
        java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());
        Expression expression = new Expression(-1, 7, Arrays.asList(1,2,3,4,5,6,7), "Some expression", creationDate);
        ExpressionLib.insertExpression(expression);
    }

    private void testSearchExpressions() throws Exception{
        Map<Article, List<Integer>> map = ExpressionLib.searchExpressionInDB(0);
        System.out.println(map);
    }

    private void myTest() throws Exception{
       String[] files = {"a", "b", "c", "d", "e", "f", "g"};
       for (String file : files){
           String path = ServerLib.getFilePath(file);
           Files.write(Paths.get(path), "lines".getBytes());
       }
    }

    private void testXmlDump() throws Exception{
        XMLDumper.buildTables();
    }

    private void testXmlLoad() throws Exception{
        ServerLib.wipeAllTables();
        XMLLoader.loadXML(new File("C:\\Users\\Gilad\\Documents\\GitHub\\databases_project\\DBParser\\temp\\dbDump.xml"));
    }

    private void testWordStats() throws Exception{
        Word word = new Word();
        word.value = "Aba";
        word.id = 2271;
        WordStats stats = WordStatLib.calculateStats(word);
        System.out.println(stats);
    }

    private void testGroupStats() throws Exception{
        WordGroup group = new WordGroup(27, 7, "Blabla");
        group.addWords(GroupLib.getGroupWords(27));

        GroupStats stats = GroupStatsLib.calculateGroupStats(group);
        System.out.println(stats);
    }


    private void testArticleStats() throws Exception{
        Article article = ArticleLib.getArticleById(63);
        ArticleStats stats = ArticleStatsLib.calculateArticleStats(article);
        System.out.println(stats);
    }

    private void testExpressionStats() throws Exception{
        Expression expression = ExpressionLib.getExpressionsById(10);
        ExpressionStats stats = ExpressionStatsLib.calculateExpressionStats(expression);
        System.out.println(stats);
    }
}
