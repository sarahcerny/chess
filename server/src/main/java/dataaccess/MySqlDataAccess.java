package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.*;
import java.util.List;

public class MySqlDataAccess implements DataAccess {

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
        throw new DataAccessException("Not implemented yet");
    }
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public int createGame(GameData game) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public GameData getGame(int gameID) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public List<GameData> listGames() throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public void updateGame(GameData game) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public void createAuth(AuthData auth) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented yet");
    }
}