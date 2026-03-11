package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess, dataAccess);
        gameService = new GameService(dataAccess, dataAccess, dataAccess);
        gameService.clear();
    }

    // lets register and see if it makes it
    @Test
    public void registerSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        assertNotNull(playerSession.authToken());
        assertEquals("sarah", playerSession.username());
    }

    @Test
    public void registerDuplicateFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        assertThrows(DataAccessException.class, () ->
                userService.register("sarah", "password", "sarah@chess.com"));
    }

    // Log in and check
    @Test
    public void loginSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        AuthData playerSession = userService.login("sarah", "password");
        assertNotNull(playerSession.authToken());
    }

    @Test
    public void loginWrongPasswordFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "wrongpassword"));
    }

    // Logging out and check
    @Test
    public void logoutSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        assertDoesNotThrow(() -> userService.logout(playerSession.authToken()));
    }

    @Test
    public void logoutInvalidTokenFails() {
        assertThrows(DataAccessException.class, () ->
                userService.logout("notreal"));
    }

    // clear data out and check
    @Test
    public void clearSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        gameService.clear();
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "password"));
    }

    // list game ideas
    @Test
    public void listGamesSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        gameService.createGame(playerSession.authToken(), "myChessGame");
        List<GameData> allChessGames = gameService.listGames(playerSession.authToken());
        assertEquals(1, allChessGames.size());
    }

    @Test
    public void listGamesUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.listGames("notreal"));
    }

    // creating the board game
    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        int newGameID = gameService.createGame(playerSession.authToken(), "myChessGame");
        assertTrue(newGameID > 0);
    }

    @Test
    public void createGameUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.createGame("notreal", "myChessGame"));
    }

    // Yaya you joined the game
    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        int newGameID = gameService.createGame(playerSession.authToken(), "myChessGame");
        assertDoesNotThrow(() -> gameService.joinGame(playerSession.authToken(), "WHITE", newGameID));
    }

    @Test
    public void joinGameAlreadyTakenFails() throws DataAccessException {
        AuthData playerSession1 = userService.register("sarah", "password", "sarah@chess.com");
        AuthData playerSession2 = userService.register("Weston", "password", "Weston@chess.com");
        int newGameID = gameService.createGame(playerSession1.authToken(), "myChessGame");
        gameService.joinGame(playerSession1.authToken(), "WHITE", newGameID);
        assertThrows(DataAccessException.class, () ->
                gameService.joinGame(playerSession2.authToken(), "WHITE", newGameID));
    }
}