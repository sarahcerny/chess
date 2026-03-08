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
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        gameService.clear();
    }
    // Register
    @Test
    public void registerSuccess() throws DataAccessException {
        AuthData auth = userService.register("sarah", "password", "sarah@email.com");
        assertNotNull(auth.authToken());
        assertEquals("sarah", auth.username());
    }

    @Test
    public void registerDuplicateFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@email.com");
        assertThrows(DataAccessException.class, () ->
                userService.register("sarah", "password", "sarah@email.com"));
    }

    // Log in
    @Test
    public void loginSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@email.com");
        AuthData auth = userService.login("sarah", "password");
        assertNotNull(auth.authToken());
    }

    @Test
    public void loginWrongPasswordFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@email.com");
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "wrongpassword"));
    }
    // Logging out
    @Test
    public void logoutSuccess() throws DataAccessException {
        AuthData auth = userService.register("sarah", "password", "sarah@email.com");
        assertDoesNotThrow(() -> userService.logout(auth.authToken()));
    }

    @Test
    public void logoutInvalidTokenFails() {
        assertThrows(DataAccessException.class, () ->
                userService.logout("faketoken"));
    }
    // clear data out
    @Test
    public void clearSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@email.com");
        gameService.clear();
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "password"));
    }
    // list game ideas
    @Test
    public void listGamesSuccess() throws DataAccessException {
        AuthData auth = userService.register("sarah", "password", "sarah@email.com");
        gameService.createGame(auth.authToken(), "testgame");
        List<GameData> games = gameService.listGames(auth.authToken());
        assertEquals(1, games.size());
    }

    @Test
    public void listGamesUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.listGames("badtoken"));
    }
    // creating the board game
    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthData auth = userService.register("sarah", "password", "sarah@email.com");
        int id = gameService.createGame(auth.authToken(), "mygame");
        assertTrue(id > 0);
    }

    @Test
    public void createGameUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.createGame("badtoken", "mygame"));
    }
    // Yaya you joined the game
    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthData auth = userService.register("sarah", "password", "sarah@email.com");
        int id = gameService.createGame(auth.authToken(), "mygame");
        assertDoesNotThrow(() -> gameService.joinGame(auth.authToken(), "WHITE", id));
    }

    @Test
    public void joinGameAlreadyTakenFails() throws DataAccessException {
        AuthData auth1 = userService.register("sarah", "password", "sarah@email.com");
        AuthData auth2 = userService.register("bob", "password", "bob@email.com");
        int id = gameService.createGame(auth1.authToken(), "mygame");
        gameService.joinGame(auth1.authToken(), "WHITE", id);
        assertThrows(DataAccessException.class, () ->
                gameService.joinGame(auth2.authToken(), "WHITE", id));
    }


}