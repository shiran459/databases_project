package app.lib;

import app.utils.ConnectionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLLoader {

    public static void loadXML(File xmlFile) throws Exception{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        NodeList tables = doc.getElementsByTagName("table");
        for (int i = 0; i < tables.getLength(); i++){
            Element article = (Element)tables.item(i);
            String tableName = article.getAttribute("name");

            List<Map> metaData = getMetadata((Element)article.getElementsByTagName("metadata").item(0));

            NodeList lines = article.getElementsByTagName("line");
            for(int j = 0; j < lines.getLength(); j++){
                Element line = (Element)lines.item(j);
                List <String> lineData = lineData(line);

                insertLine(tableName, metaData, lineData);
            }
        }
    }

    private static List<Map> getMetadata (Element metadataNod) {
        List<Map> res = new ArrayList<>();
        NodeList columms = metadataNod.getElementsByTagName("column");
        for (int i = 0; i < columms.getLength(); i++){
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", columms.item(i).getTextContent());
            data.put("type", getColType(((Element)columms.item(i)).getAttribute("type")));
            res.add(data);
        }

        return res;
    }

    private static List<String> lineData (Element line) {
        List<String> lineData = new ArrayList<>();
        NodeList columns = line.getElementsByTagName("column");

        for(int i = 0; i < columns.getLength(); i++){
            lineData.add(columns.item(i).getTextContent());
        }

        return lineData;
    }

    private static ColType getColType(String type){
        if (type.toLowerCase().contains("number"))
            return ColType.INT;
        else if (type.toLowerCase().contains("date"))
            return ColType.DATE;
        else
            return ColType.STRING;
    }

    private static void insertLine(String tableName, List<Map> metadata, List<String> lineData) throws Exception{
        String sql = "INSERT INTO " + tableName + "(" + metadata.get(0).get("name");
        for(int i = 1; i < metadata.size(); i++){
            sql = sql + "," + metadata.get(i).get("name");
        }
        sql += ") VALUES (?";
        for (int i = 0; i < metadata.size()-1; i++)
            sql += ",?";
        sql += ")";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        for (int i = 0; i < metadata.size(); i++){
            setData(pstmt, lineData.get(i), i+1, (ColType)metadata.get(i).get("type"));
        }
        pstmt.executeUpdate();
        pstmt.close();
    }

    private static void setData(PreparedStatement pstmt, String data, int index, ColType type) throws Exception{
        switch (type){
            case STRING:
                pstmt.setString(index, data);
                break;
            case INT:
                pstmt.setInt(index, Integer.parseInt(data));
                break;
            case DATE:
                if(data.length() > 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                    Date date = formatter.parse(data);
                    long mills = date.getTime();
                    pstmt.setDate(index, new java.sql.Date(mills));
                } else {
                    pstmt.setDate(index,null);
                }
                break;
            default:
                break;
        }
    }

    enum ColType {
        INT,
        DATE,
        STRING,
    }
}
