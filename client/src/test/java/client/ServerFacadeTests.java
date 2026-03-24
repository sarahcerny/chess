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


}
