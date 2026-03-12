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


}
