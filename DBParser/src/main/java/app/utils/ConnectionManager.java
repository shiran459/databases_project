package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

    private static Connection con = null;

    public static Connection getConnection(){
        if (con == null){
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:orcl", "shiran", "password");
            } catch (Exception e) {
                e.printStackTrace();
            }//TODO: Make sure con!=null
        }
        return con;
    }
}
