package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import chess.ChessGame;
import java.util.ArrayList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlDataAccess implements DataAccess {

    private final Gson gson = new Gson();
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String[] createStatements = {
                    """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    PRIMARY KEY (username)
                )
                ""\",
                    ""\"
                CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(255) NOT NULL,
                    username VARCHAR(50) NOT NULL,
                    PRIMARY KEY (authToken)
                )
                ""\",
                    ""/"
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(50),
                    blackUsername VARCHAR(50),
                    gameName VARCHAR(100) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (gameID)
                )
                """
            };
            for (var statement : createStatements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var table : new String[]{"users", "auth", "games"}) {
                try (var ps = conn.prepareStatement("TRUNCATE TABLE " + table)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear database", e);
        }
    }

    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, user.username());
                ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT username, password, email FROM users WHERE username=?")) {
                ps.setString(1, username);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
        return null;
    }

    public int createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, gson.toJson(game.game()));
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) { return rs.getInt(1); }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
        return 0;
    }
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM games WHERE gameID=?")) {
                ps.setInt(1, gameID);
                var rs = ps.executeQuery();
                if (rs.next()) { return readGame(rs); }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
        return null;
    }
    public List<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM games")) {
                var rs = ps.executeQuery();
                while (rs.next()) { games.add(readGame(rs)); }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games", e);
        }
        return games;
    }
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?")) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, gson.toJson(game.game()));
                ps.setInt(5, game.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, auth.authToken());
                ps.setString(2, auth.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth", e);
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
        return null;
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete auth", e);
        }
    }
}