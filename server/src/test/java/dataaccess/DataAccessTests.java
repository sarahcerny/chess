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


public class DAOTests {

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


}
