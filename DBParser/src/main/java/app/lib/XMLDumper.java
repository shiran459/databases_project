package app.lib;

import app.utils.ConnectionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class XMLDumper {
    public static final String[] tables = {"ARTICLES", "WORDS", "USERS", "WORD_INDEX", "EXPRESSIONS", "GROUPS", "GROUP_WORDS" };

    public static Path buildTables() throws Exception {
        Document doc = getDocument();

        Element rootElement = doc.createElement("DB");
        doc.appendChild(rootElement);

        for (String tableName : tables) {
            //create table tag
            Element table = doc.createElement("table");
            table.setAttribute("name", tableName);
            rootElement.appendChild(table);

            //create metadata
            Element metaData = doc.createElement("metadata");
            table.appendChild(metaData);
            metaData.appendChild(buildColumnData(tableName, doc));

            //create lines
            Element lines = buildLines(tableName, doc);
            table.appendChild(lines);
        }

        return storeResults(doc);
    }

    private static Document getDocument() throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    private static Path storeResults(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        Path dictPath =  Paths.get(System.getProperty("user.dir"),
                "DBParser",
                "temp");
        Files.createDirectories(dictPath);
        Path filePath = dictPath.resolve("dbDump.xml");

        StreamResult result = new StreamResult(filePath.toFile());

        transformer.transform(source, result);

        return filePath;
    }

    private static Element buildColumnData(String tableName, Document doc) throws Exception {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE " +
                "FROM all_tab_cols " +
                "WHERE TABLE_NAME = ? " +
                "ORDER BY column_id";

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        pstmt.setString(1, tableName);
        ResultSet res = pstmt.executeQuery();

        Element rootElement = doc.createElement("columns");

        while (res.next()) {
            String name = res.getString("COLUMN_NAME");
            String type = res.getString("DATA_TYPE");
            Element column = doc.createElement("column");
            column.setAttribute("type", type);
            column.setTextContent(name);

            rootElement.appendChild(column);
        }

        res.close();
        pstmt.close();

        return rootElement;
    }

    private static Element buildLines(String tableName, Document doc) throws Exception {
        String sql = "SELECT * " +
                "FROM " + tableName;

        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);
        ResultSet res = pstmt.executeQuery();

        Element rootElement = doc.createElement("lines");

        while (res.next()) {
            Element line = doc.createElement("line");
            ResultSetMetaData rsmd = res.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                Element column = doc.createElement("column");
                column.setTextContent(res.getString(i));
                line.appendChild(column);
            }
            rootElement.appendChild(line);
        }

        res.close();
        pstmt.close();

        return rootElement;
    }
}
