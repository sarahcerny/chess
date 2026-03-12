package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class SqlUserDAO implements UserDAO {

    // spin up the tables when we first create this bad boy
    public SqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    // sets up all three tables if they dont exist yet periodt
    private void configureDatabase() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String[] statements = {
                    // users table stores everyone who signed up slay
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "username VARCHAR(256) NOT NULL, " +
                            "password VARCHAR(256) NOT NULL, " +
                            "email VARCHAR(256) NOT NULL, " +
                            "PRIMARY KEY (username))",
                    // auth table keeps track of who is logged in rn
                    "CREATE TABLE IF NOT EXISTS auth (" +
                            "authToken VARCHAR(256) NOT NULL, " +
                            "username VARCHAR(256) NOT NULL, " +
                            "PRIMARY KEY (authToken))",
                    // games table every chess game that has ever existed king
                    "CREATE TABLE IF NOT EXISTS games (" +
                            "gameID INT NOT NULL AUTO_INCREMENT, " +
                            "whiteUsername VARCHAR(256), " +
                            "blackUsername VARCHAR(256), " +
                            "gameName VARCHAR(256) NOT NULL, " +
                            "game TEXT NOT NULL, " +
                            "PRIMARY KEY (gameID))"
            };
            // run each create statement one by one no shortcuts bestie
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

    // storing plain text passwords is NOT the vibe we protect our players here
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            ps.setString(1, user.username());
            // bcrypt goes crazy encrypting this password so nobody can steal it
            ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    // look up a player by username
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username, password, email FROM users WHERE username=?")) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            // if they exist pull them out and send them back slay
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
        // temp player not found
        return null;
    }
}