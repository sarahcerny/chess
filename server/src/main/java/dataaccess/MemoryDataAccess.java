package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> realPlayers = new HashMap<>();
    private final Map<Integer, GameData> activeGames = new HashMap<>();
    private final Map<String, AuthData> activePlayers = new HashMap<>();
    private int futureGameID = 1;

    public void clear() {
        realPlayers.clear();
        activeGames.clear();
        activePlayers.clear();
        futureGameID = 1;
    }

    public void createUser(UserData user) throws DataAccessException {
        realPlayers.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return realPlayers.get(username);
    }

    public int createGame(GameData game) throws DataAccessException {
        int id = futureGameID++;
        GameData withID = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        activeGames.put(id, withID);
        return id;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return activeGames.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(activeGames.values());
    }

    public void updateGame(GameData game) throws DataAccessException {
        activeGames.put(game.gameID(), game);
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        activePlayers.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return activePlayers.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        activePlayers.remove(authToken);
    }
}