package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

// handles everything player account related
public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // brand new player wants in deal them
    public AuthData register(String playerName, String password, String playerEmail) throws DataAccessException {
        if (playerName == null || password == null || playerEmail == null) {
            throw new DataAccessException("Error: bad request");
        }
        // already got someone with that name no identity fraud here sis
        if (userDAO.getUser(playerName) != null) {
            throw new DataAccessException("Error: already taken");
        }
        // all good fresh token for them game time yay
        userDAO.createUser(new UserData(playerName, password, playerEmail));
        AuthData playerSession = new AuthData(UUID.randomUUID().toString(), playerName);
        authDAO.createAuth(playerSession);
        return playerSession;
    }

    // check their password and hook them up with a new token
    public AuthData login(String playerName, String password) throws DataAccessException {
        // missing info bad request no cap
        if (playerName == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }
        // look them up in the system aka stalk
        UserData playerData = userDAO.getUser(playerName);
        // either they dont exist or wrong password
        if (playerData == null || !org.mindrot.jbcrypt.BCrypt.checkpw(password, playerData.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        // yay they passed
        AuthData playerSession = new AuthData(UUID.randomUUID().toString(), playerName);
        authDAO.createAuth(playerSession);
        return playerSession;
    }

    // player is done for now
    public void logout(String playerToken) throws DataAccessException {
        if (authDAO.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // token goes bye bye
        authDAO.deleteAuth(playerToken);
    }
}