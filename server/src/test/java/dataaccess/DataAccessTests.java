package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;
// import service.AuthService;
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

    // calling cap on token returns null not an explosion we stay calm here
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






}
