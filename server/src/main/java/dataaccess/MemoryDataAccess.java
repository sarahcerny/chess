package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// stores everything in memory for now
public class MemoryDataAccess implements DataAccess {

    // all the real players who have signed up
    private final Map<String, UserData> realPlayers = new HashMap<>();

    // every chess game that has been created
    private final Map<Integer, GameData> activeGames = new HashMap<>();
    // players who are currently logged in and locked in with tokens
    private final Map<String, AuthData> activePlayers = new HashMap<>();

    // keeps track of what id to give the next game
    private int futureGameID = 1;

    // wipe everything fresh slate baby
    public void clear() {
        realPlayers.clear();
        activeGames.clear();
        activePlayers.clear();
        // reset the counter too or ids get weird
        futureGameID = 1;
    }


    // welcome to the game new player add them to the map bc they in it
    public void createUser(UserData user) throws DataAccessException {
        realPlayers.put(user.username(), user);
    }
    // look up a player by username returns null if they arent with it
    public UserData getUser(String username) throws DataAccessException {
        return realPlayers.get(username);
    }
    // create a new game and give it a real id
    public int createGame(GameData game) throws DataAccessException {
        int id = futureGameID++;
        GameData withID = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        activeGames.put(id, withID);
        return id;
    }
    // find a specific game by its id null if THEY AINT REAL
    public GameData getGame(int gameID) throws DataAccessException {
        return activeGames.get(gameID);
    }
    // dump all the games into a list so we can send them back
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(activeGames.values());
    }

    // swap out an old game with an new fresh version
    public void updateGame(GameData game) throws DataAccessException {
        activeGames.put(game.gameID(), game);
    }

    // player just logged in
    public void createAuth(AuthData auth) throws DataAccessException {
        activePlayers.put(auth.authToken(), auth);
    }

    // check if a token is legit
    public AuthData getAuth(String authToken) throws DataAccessException {
        return activePlayers.get(authToken);
    }

    // player logged out
    public void deleteAuth(String authToken) throws DataAccessException {
        activePlayers.remove(authToken);
    }
}