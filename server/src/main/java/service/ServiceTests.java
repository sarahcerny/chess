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




}