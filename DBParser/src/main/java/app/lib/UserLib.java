package app.lib;

import app.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserLib {
    /**
     * Inserts a user into the USERS table.
     *
     * @param username            Username chosen by the user.
     * @param passwordHash        Hash for the user's password.
     * @param sessionToken        Token for a user's current website session.
     * @param tokenExpirationTime Expiration time of the current session token.
     * @throws SQLException In case the transaction has failed.
     */
    public static void insertUser(String username, String passwordHash, String sessionToken, Timestamp tokenExpirationTime)
            throws SQLException {
        String sql = "INSERT INTO users(user_id, username, password_hash, session_token, token_expiration_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = ConnectionManager.getConnection().prepareStatement(sql);

            pstmt.setNull(1, java.sql.Types.INTEGER);
            pstmt.setString(2, username);
            pstmt.setString(3, passwordHash);
            pstmt.setString(4, sessionToken);
            pstmt.setTimestamp(5, tokenExpirationTime);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
