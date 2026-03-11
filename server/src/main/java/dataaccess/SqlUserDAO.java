package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String[] statements = {
                    "CREATE TABLE IF NOT EXISTS users (username VARCHAR(256) NOT NULL, password VARCHAR(256) NOT NULL, email VARCHAR(256) NOT NULL, PRIMARY KEY (username))",
                    "CREATE TABLE IF NOT EXISTS auth (authToken VARCHAR(256) NOT NULL, username VARCHAR(256) NOT NULL, PRIMARY KEY (authToken))",
                    "CREATE TABLE IF NOT EXISTS games (gameID INT NOT NULL AUTO_INCREMENT, whiteUsername VARCHAR(256), blackUsername VARCHAR(256), gameName VARCHAR(256) NOT NULL, game TEXT NOT NULL, PRIMARY KEY (gameID))"
            };
            for (var statement : statements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("TRUNCATE TABLE users")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear users", e);
        }
    }

    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            ps.setString(1, user.username());
            ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username, password, email FROM users WHERE username=?")) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
        return null;
    }
}