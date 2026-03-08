package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.GameData;
import service.GameService;
import service.UserService;

import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;

    // using gson for json stuff throughout
    private final Gson gson = new Gson();

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");

            // wire in gson so javalin uses it instead of the default
            Gson g = new Gson();
            config.jsonMapper(new JsonMapper() {
                @Override
                public String toJsonString(Object obj, java.lang.reflect.Type type) {
                    return g.toJson(obj, type);
                }
                @Override
                public <T> T fromJsonString(String json, java.lang.reflect.Type targetType) {
                    return g.fromJson(json, targetType);
                }
            });
        });

        registerRoutes();
        registerExceptionHandlers();
    }

    private void registerRoutes() {
        javalin.delete("/db",      this::clear);
        javalin.post("/user",      this::register);
        javalin.post("/session",   this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game",       this::listGames);
        javalin.post("/game",      this::createGame);
        javalin.put("/game",       this::joinGame);
    }

    private void registerExceptionHandlers() {
        javalin.exception(DataAccessException.class, this::handleDataAccessException);
        javalin.exception(Exception.class, this::handleGenericException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // DELETE /db
    private void clear(Context ctx) throws DataAccessException {
        gameService.clear();
        ctx.status(200).json(Map.of());
    }

    // POST /user
    private void register(Context ctx) throws DataAccessException {
        var body = gson.fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String email    = (String) body.get("email");

        var auth = userService.register(username, password, email);
        ctx.status(200).json(Map.of("username", auth.username(), "authToken", auth.authToken()));
    }

    // POST /session
    private void login(Context ctx) throws DataAccessException {
        var body = gson.fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        var auth = userService.login(username, password);
        ctx.status(200).json(Map.of("username", auth.username(), "authToken", auth.authToken()));
    }

    // DELETE /session
    private void logout(Context ctx) throws DataAccessException {
        String token = ctx.header("authorization");
        userService.logout(token);
        ctx.status(200).json(Map.of());
    }

    // GET /game
    private void listGames(Context ctx) throws DataAccessException {
        String token = ctx.header("authorization");
        List<GameData> games = gameService.listGames(token);
        ctx.status(200).json(Map.of("games", games));
    }

    // POST /game
    private void createGame(Context ctx) throws DataAccessException {
        String token = ctx.header("authorization");
        var body = gson.fromJson(ctx.body(), Map.class);
        String gameName = (String) body.get("gameName");

        int id = gameService.createGame(token, gameName);
        ctx.status(200).json(Map.of("gameID", id));
    }

    // PUT /game
    private void joinGame(Context ctx) throws DataAccessException {
        String token = ctx.header("authorization");
        var body = gson.fromJson(ctx.body(), Map.class);

        String playerColor = (String) body.get("playerColor");
        Object rawID = body.get("gameID");

        if (rawID == null) {
            throw new DataAccessException("Error: bad request");
        }

        // gson parses numbers as Double by default
        int gameID = ((Double) rawID).intValue();
        gameService.joinGame(token, playerColor, gameID);
        ctx.status(200).json(Map.of());
    }

    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String msg = e.getMessage();

        if (msg.contains("bad request")) {
            ctx.status(400);
        } else if (msg.contains("unauthorized")) {
            ctx.status(401);
        } else if (msg.contains("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }

        ctx.json(Map.of("message", msg));
    }

    private void handleGenericException(Exception e, Context ctx) {
        ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
    }
}