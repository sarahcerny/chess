package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import service.GameService;
import service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        javalin.exception(DataAccessException.class, this::handleDataAccessException);
        javalin.exception(Exception.class, this::handleException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) throws DataAccessException {
        gameService.clear();
        ctx.status(200).json(Map.of());
    }

    private void register(Context ctx) throws DataAccessException {
        var body = gson.fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String email = (String) body.get("email");
        var auth = userService.register(username, password, email);
        ctx.status(200).json(Map.of("username", auth.username(), "authToken", auth.authToken()));
    }

    private void login(Context ctx) throws DataAccessException {
        var body = gson.fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        var auth = userService.login(username, password);
        ctx.status(200).json(Map.of("username", auth.username(), "authToken", auth.authToken()));
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200).json(Map.of());
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        List<GameData> games = gameService.listGames(authToken);
        ctx.status(200).json(Map.of("games", games));
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        var body = gson.fromJson(ctx.body(), Map.class);
        String gameName = (String) body.get("gameName");
        int gameID = gameService.createGame(authToken, gameName);
        ctx.status(200).json(Map.of("gameID", gameID));
    }

    private void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        var body = gson.fromJson(ctx.body(), Map.class);
        String playerColor = (String) body.get("playerColor");
        int gameID = ((Double) body.get("gameID")).intValue();
        gameService.joinGame(authToken, playerColor, gameID);
        ctx.status(200).json(Map.of());
    }

    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String message = e.getMessage();
        if (message.contains("bad request")) {
            ctx.status(400);
        } else if (message.contains("unauthorized")) {
            ctx.status(401);
        } else if (message.contains("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }
        ctx.json(Map.of("message", message));
    }

    private void handleException(Exception e, Context ctx) {
        ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
    }
}