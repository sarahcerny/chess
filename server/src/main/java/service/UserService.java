package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(String playerName, String password, String playerEmail) throws DataAccessException {
        if (playerName == null || password == null || playerEmail == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getUser(playerName) != null) {
            throw new DataAccessException("Error: already taken");
        }
        dataAccess.createUser(new UserData(playerName, password, playerEmail));
        AuthData playerSession = new AuthData(UUID.randomUUID().toString(), playerName);
        dataAccess.createAuth(playerSession);
        return playerSession;
    }

    public AuthData login(String playerName, String password) throws DataAccessException {
        if (playerName == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData playerData = dataAccess.getUser(playerName);
        if (playerData == null || !playerData.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData playerSession = new AuthData(UUID.randomUUID().toString(), playerName);
        dataAccess.createAuth(playerSession);
        return playerSession;
    }

    public void logout(String playerToken) throws DataAccessException {
        if (dataAccess.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(playerToken);
    }
}