package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private UserService userService;
    private GameService gameService;

    // runs before every single test fresh start so nothing overflows
    @BeforeEach
    public void setup() throws DataAccessException {
        var userDAO = new MemoryUserDAO();
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
        //gotta clean it clear
        gameService.clear();
    }

    // lets register and see if it makes it
    //new player signs up and gets a token back yay
    @Test
    public void registerSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        assertNotNull(playerSession.authToken());
        assertEquals("sarah", playerSession.username());
    }
    // no doppelganger sorry sarah #2
    @Test
    public void registerDuplicateFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        assertThrows(DataAccessException.class, () ->
                userService.register("sarah", "password", "sarah@chess.com"));
    }

    // Log in and check
    // register first then login should get a fresh token no problem
    @Test
    public void loginSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        AuthData playerSession = userService.login("sarah", "password");
        assertNotNull(playerSession.authToken());
    }
    // wrong password get the helly out of here boss
    @Test
    public void loginWrongPasswordFails() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "wrongpassword"));
    }

    // Logging out and check no drama plz
    @Test
    public void logoutSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        assertDoesNotThrow(() -> userService.logout(playerSession.authToken()));
    }
    // fake token should get rejected lol nice try
    @Test
    public void logoutInvalidTokenFails() {
        assertThrows(DataAccessException.class, () ->
                userService.logout("notreal"));
    }

    // after clear sarah shouldnt exist anymore wipe me down
    @Test
    public void clearSuccess() throws DataAccessException {
        userService.register("sarah", "password", "sarah@chess.com");
        gameService.clear();
        assertThrows(DataAccessException.class, () ->
                userService.login("sarah", "password"));
    }

    // list game ideas
    // one for one game
    @Test
    public void listGamesSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        gameService.createGame(playerSession.authToken(), "myChessGame");
        List<GameData> allChessGames = gameService.listGames(playerSession.authToken());
        assertEquals(1, allChessGames.size());
    }


    // no token no games get ya ah out of here girl
    @Test
    public void listGamesUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.listGames("notreal"));
    }

    // creating the board game
    // make a game and get back a valid id but never 0 my friend
    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        int newGameID = gameService.createGame(playerSession.authToken(), "myChessGame");
        assertTrue(newGameID > 0);
    }

    // random bad token shouldnt be able to make games lol

    @Test
    public void createGameUnauthorizedFails() {
        assertThrows(DataAccessException.class, () ->
                gameService.createGame("notreal", "myChessGame"));
    }

    // Yaya you joined the game
    // sarah picks white and joins should work smooth operator
    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthData playerSession = userService.register("sarah", "password", "sarah@chess.com");
        int newGameID = gameService.createGame(playerSession.authToken(), "myChessGame");
        assertDoesNotThrow(() -> gameService.joinGame(playerSession.authToken(), "WHITE", newGameID));
    }


    // sarah takes white then weston tries to steal it but cant finders keepers
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