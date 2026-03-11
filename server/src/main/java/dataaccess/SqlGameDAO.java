package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("TRUNCATE TABLE games")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games", e);
        }
    }

    public int createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gson.toJson(game.game()));
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) { return rs.getInt(1); }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT * FROM games WHERE gameID=?")) {
            ps.setInt(1, gameID);
            var rs = ps.executeQuery();
            if (rs.next()) { return readGame(rs); }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
        return null;
    }

    public List<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT * FROM games")) {
            var rs = ps.executeQuery();
            while (rs.next()) { games.add(readGame(rs)); }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games", e);
        }
        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?")) {
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gson.toJson(game.game()));
            ps.setInt(5, game.gameID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                gson.fromJson(rs.getString("game"), ChessGame.class)
        );
    }
}