package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private int nextGameID = 1;

    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
    }

    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public int createGame(GameData game) throws DataAccessException {
        int id = nextGameID++;
        games.put(id, new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        return id;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}