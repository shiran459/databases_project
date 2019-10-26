import java.sql.*;

public class OracleConnection {
    //====================== INSTANCE VARIABLES =====================//
    private Connection con;

    //=========================== CONSTRUCTOR =======================//
    public OracleConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //============================= METHODS =========================//
    public void connect() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
            Statement stmt = con.createStatement();

            //step4 execute query
            ResultSet rs = stmt.executeQuery("select * from emp");
            while (rs.next())
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));

            //step5 close the connection object
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

