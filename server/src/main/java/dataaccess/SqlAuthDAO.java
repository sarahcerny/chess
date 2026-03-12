package dataaccess;

import model.AuthData;
import java.sql.*;

public class SqlAuthDAO implements AuthDAO {



    //fresh slate everyone gets logged out
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("TRUNCATE TABLE auth")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth", e);
        }
    }

    // player just logged in store their fresh token
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth", e);
        }
    }


    // check if a token is legit and who it belongs to
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT * FROM auth WHERE authToken=?")) {
            ps.setString(1, authToken);
            var rs = ps.executeQuery();
            // found them pull the auth data and send it back
            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
        return null;
    }
    // player logged out yeet their token into the void bye

    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete auth", e);
        }
    }
}