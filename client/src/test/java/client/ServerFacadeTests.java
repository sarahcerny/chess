package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void resetFacade() throws Exception {
        facade.clearDatabase();
        facade.resetSession();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clearDatabase();
    }

    // Register
    @Test
    void registerPositive() throws Exception {
        String token = facade.register("Sarah", "sarah123", "sarah@email.com");
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.length() > 10);
    }

    @Test
    void registerNegative() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.register("Ellie", "ellie123", "ellie@email.com");
            facade.register("Ellie", "ellie123", "ellie@email.com"); // duplicate
        });
    }

    // login i guess
    @Test
    void loginPositive() throws Exception {
        facade.register("Maddy", "maddy123", "maddy@email.com");
        String token = facade.login("Maddy", "maddy123");
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.length() > 10);
    }

    @Test
    void loginNegative() {
        Assertions.assertThrows(Exception.class, () ->
                facade.login("Weston", "wrongpass")
        );
    }

    // Logout now queen
    @Test
    void logoutPositive() throws Exception {
        facade.register("Tyler", "tyler123", "tyler@email.com");
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    void logoutNegative() {
        Assertions.assertThrows(Exception.class, () ->
                facade.logout()
        );
    }

    // lets create the game
    @Test
    void createGamePositive() throws Exception {
        facade.register("Anna", "anna123", "anna@email.com");
        Assertions.assertDoesNotThrow(() -> facade.createGame("ChessGame"));
    }

    @Test
    void createGameNegative() {
        Assertions.assertThrows(Exception.class, () ->
                facade.createGame("ChessGame")
        );
    }

    // now lets list the games.
    @Test
    void listGamesPositive() throws Exception {
        facade.register("Jonas", "jonas123", "jonas@email.com");
        facade.createGame("ChessGame1");
        facade.createGame("ChessGame2");
        List<GameData> games = facade.listGames();
        Assertions.assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() {
        Assertions.assertThrows(Exception.class, () ->
                facade.listGames()
        );
    }
    // lets join a game of chess
    @Test
    void joinGamePositive() throws Exception {
        facade.register("Lane", "lane123", "lane@email.com");
        int gameId = facade.createGame("JoinChess");
        Assertions.assertDoesNotThrow(() -> facade.joinGame(gameId, "WHITE"));
    }

    @Test
    void joinGameNegative() throws Exception {
        facade.register("Emma", "emma123", "emma@email.com");
        Assertions.assertThrows(Exception.class, () ->
                facade.joinGame(9999, "WHITE") // invalid game id
        );
    }

    // find that session token
    @Test
    void getAuthTokenPositive() throws Exception {
        facade.register("Sarah", "sarah123", "sarah@email.com");
        Assertions.assertNotNull(facade.getAuthToken());
    }

    @Test
    void getAuthTokenNegative() {
        Assertions.assertNull(facade.getAuthToken()); // before login
    }

    //
    @Test
    void getUsernamePositive() throws Exception {
        facade.register("Ellie", "ellie123", "ellie@email.com");
        Assertions.assertEquals("Ellie", facade.getUsername());
    }

    @Test
    void getUsernameNegative() {
        Assertions.assertNull(facade.getUsername()); // before login
    }






}
