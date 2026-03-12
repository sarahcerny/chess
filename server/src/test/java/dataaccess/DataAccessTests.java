package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;
import service.GameService;
import javax.xml.crypto.Data;



import static org.junit.jupiter.api.Assertions.*;


public class DataAccessTests {

    private SqlUserDAO userDAO;
    private SqlAuthDAO authDAO;
    private SqlGameDAO gameDAO;

    @BeforeEach
    public void freshStart() throws DataAccessException {
        userDAO = new SqlUserDAO();
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();
        // wipe it all clean before every test no leftovers
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
    // clear goes crazy and deletes everyone bye felicia
    @Test
    public void clearUserInput() throws DataAccessException {
        userDAO.createUser(new UserData("weston", "password", "weston@chess.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("weston"));
    }

    // brand new player gets added to the roster slay
    @Test
    public void newPlayerAdded() throws DataAccessException {
        userDAO.createUser(new UserData("weston", "password", "weston@chess.com"));
        assertNotNull(userDAO.getUser("weston"));
    }
    // no identity fraud allowed same username twice not on my watch king
    @Test
    public void twinUsernameFails() throws DataAccessException {
        userDAO.createUser(new UserData("weston", "password", "weston@chess.com"));
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData("weston", "password", "sarah@chess.com")));
    }

    // look up a real player and they show up period
    @Test
    public void findUserPass() throws DataAccessException {
        userDAO.createUser(new UserData("weston", "password", "weston@chess.com"));
        UserData found = userDAO.getUser("weston");
        assertEquals("weston", found.username());
        assertEquals("weston@chess.com", found.email());
    }
    // tempplayer doesnt exist returns null
    @Test
    public void isnaUserTurnsNull() throws DataAccessException {
        assertNull(userDAO.getUser("tempplayer"));
    }
    // clear kills all tokens no survivors slay
    @Test
    public void clearTokenSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("supertoken", "weston"));
        authDAO.clear();
        assertNull(authDAO.getAuth("supertoken"));
    }

    // new token gets stored king we love to see it
    @Test
    public void addTokenSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("supertoken", "weston"));
        assertNotNull(authDAO.getAuth("supertoken"));
    }
    // duplicate token is a big no allowed two keys to the same lock no way
    @Test
    public void twinAuthFails() throws DataAccessException {
        authDAO.createAuth(new AuthData("supertoken", "weston"));
        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(new AuthData("supertoken", "sarah")));
    }
    // token comes back with the right username attached slay
    @Test
    public void findAuthPass() throws DataAccessException {
        authDAO.createAuth(new AuthData("supertoken", "weston"));
        AuthData result = authDAO.getAuth("supertoken");
        assertEquals("weston", result.username());
    }

    // calling cap on token returns null stay calm quota
    @Test
    public void isnaAuthTurnsNull() throws DataAccessException {
        assertNull(authDAO.getAuth("captoken"));
    }

    // player logs out token gets yeeted
    @Test
    public void logoutTokenSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("supertoken", "weston"));
        authDAO.deleteAuth("supertoken");
        assertNull(authDAO.getAuth("supertoken"));
    }
    // faketoken never existed shouldnt blow up we are unbothered
    @Test
    public void deleteTempToken() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("faketoken"));
    }
    // clear wipes all games gone gone gone periodt
    @Test
    public void allGamesClear() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "goodgame", new ChessGame()));
        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }

    // new game gets created and returns a real id not 0 slay king
    @Test
    public void newGameGo() throws DataAccessException {
        int id = gameDAO.createGame(new GameData(0, null, null, "goodgame", new ChessGame()));
        assertTrue(id > 0);
    }

    // null game name should blow up bc every game needs a name bestie
    @Test
    public void nullNoNameFails() {
        assertThrows(DataAccessException.class, () ->
                gameDAO.createGame(new GameData(0, null, null, null, new ChessGame())));
    }
    // get back the right game by id we love accuracy
    @Test
    public void findGameWorks() throws DataAccessException {
        int id = gameDAO.createGame(new GameData(0, null, null, "goodgame", new ChessGame()));
        GameData found = gameDAO.getGame(id);
        assertEquals("goodgame", found.gameName());
    }
    @Test
    public void notRealIdIsNull() throws DataAccessException {
        assertNull(gameDAO.getGame(95257));
    }

    // two games in the list
    @Test
    public void gameListWorks() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "game2", new ChessGame()));
        assertEquals(2, gameDAO.listGames().size());
    }

    // empty list is valid too not an error we respect the empty era
    @Test
    public void emptyListWorks() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }
    // sarah joins white and we can see it saved slay get it girl
    @Test
    public void playerAddedToGame() throws DataAccessException {
        int id = gameDAO.createGame(new GameData(0, null, null, "goodgame", new ChessGame()));
        gameDAO.updateGame(new GameData(id, "sarah", null, "goodgame", new ChessGame()));
        GameData updated = gameDAO.getGame(id);
        assertEquals("sarah", updated.whiteUsername());
    }

    // updating game 95257 that doesnt exist shouldnt crash we are professionals
    @Test
    public void tempGameWorks() {
        assertDoesNotThrow(() ->
                gameDAO.updateGame(new GameData(95257, "sarah", null, "goodgame", new ChessGame())));
    }


}
