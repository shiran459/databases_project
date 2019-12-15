package app.lib;

import app.utils.ConnectionManager;
import app.utils.User;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLib {

    public static User register(String username, String password) throws Exception{
        if (!isUsernameAvailable(username))
            return null;

        //Create new user
        String passwordHash = hashString(password);
        String token = generateSessionToken();
        User user = insertUser(username, passwordHash, token);

        return user;
    }

    public static User login(String username, String password) throws Exception {
        String passwordHash = hashString(password);
        //Check if password is correct -> if not: return null
        if (!isPasswordCorrect(username, passwordHash))
            return null;

        String token = generateSessionToken();
        saveToken(username, token);
        User user = getUserByUsername(username);

        return user;
    }

    public static User currentUser(String token) throws SQLException{
        String sql = "SELECT user_id, username " +
                "FROM users " +
                "WHERE session_token = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

        pstmt.setString(1, token);
        ResultSet res = pstmt.executeQuery();
        User currentUser = null;
        if (res.next()) {
            int id = res.getInt("user_id");
            String username = res.getString("username");

            currentUser= new User(id, username);
        }
        pstmt.close();
        res.close();
        return currentUser;
    }

    public static User extractUser(HttpServletRequest request){
        return (User)request.getAttribute("current_user");
    }

    public static boolean isUsernameAvailable(String username) throws SQLException{
        String sql = "SELECT user_id " +
                "FROM users " +
                "WHERE username = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

        pstmt.setString(1, username);
        ResultSet res = pstmt.executeQuery();

        boolean result = true;
        if (res.next()) {
            result = false;
        }

        pstmt.close();
        res.close();

        return result;
    }

    /**
     * Inserts a user into the USERS table.
     *
     * @param username            Username chosen by the user.
     * @param passwordHash        Hash for the user's password.
     * @throws SQLException In case the transaction has failed.
     */
    public static User insertUser(String username, String passwordHash, String token)
            throws SQLException {
        String sql = "INSERT INTO users(user_id, username, password_hash, session_token) " +
                "VALUES (users_seq.NEXTVAL, ?, ?, ?)";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql,new String[]{"user_id"});
        pstmt.setString(1, username);
        pstmt.setString(2, passwordHash);
        pstmt.setString(3, token);

        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt(1);
        }

        User user = new User(id, username);
        user.token = token;

        pstmt.close();
        rs.close();

        return user;
    }

    public static User getUserByUsername(String username) throws SQLException{
        String sql = "SELECT user_id, session_token " +
                "FROM users WHERE username = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

        pstmt.setString(1, username);
        ResultSet res = pstmt.executeQuery();

        User user = null;
        while (res.next()) {
            int id =  res.getInt("user_id");
            String token = res.getString("session_token");
            user = new User(id, username);
            user.token = token;
        }

        return user;
    }

    public static boolean isPasswordCorrect(String username, String passwordHash) throws SQLException{
        String sql = "SELECT password_hash " +
                "FROM users WHERE username = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

        pstmt.setString(1, username);
        ResultSet res = pstmt.executeQuery();

        String storedHash = "";
        while (res.next()) {
           storedHash = res.getString("password_hash");
        }

        return storedHash.equals(passwordHash);
    }

    public static void saveToken(String username, String token) throws SQLException{

        String sql = "UPDATE users " +
                "SET session_token = ? " +
                "WHERE username = ?";
        PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

        pstmt.setString(1, token);
        pstmt.setString(2, username);

        pstmt.executeUpdate();

        pstmt.close();
    }

    public static String hashString(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
            str.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < encodedhash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedhash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generateSessionToken() throws NoSuchAlgorithmException{
        String currentTime = Long.toString(System.currentTimeMillis());
        return hashString(currentTime);
    }
}
