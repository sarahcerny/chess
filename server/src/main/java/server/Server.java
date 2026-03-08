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

    private final Javalin myServer;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gsonMap = new Gson();

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        myServer = Javalin.create(config -> {
            config.staticFiles.add("web");
            Gson mapper = new Gson();
            config.jsonMapper(new JsonMapper() {
                @Override
                public String toJsonString(Object obj, java.lang.reflect.Type type) {
                    return mapper.toJson(obj, type);
                }
                @Override
                public <T> T fromJsonString(String json, java.lang.reflect.Type targetType) {
                    return mapper.fromJson(json, targetType);
                }
            });
        });

        registerRoutes();
        registerExceptionHandlers();
    }

    private void registerRoutes() {
        myServer.delete("/db",      this::clear);
        myServer.post("/user",      this::register);
        myServer.post("/session",   this::login);
        myServer.delete("/session", this::logout);
        myServer.get("/game",       this::listGames);
        myServer.post("/game",      this::createGame);
        myServer.put("/game",       this::joinGame);
    }

    private void registerExceptionHandlers() {
        myServer.exception(DataAccessException.class, this::handleDataAccessException);
        myServer.exception(Exception.class, this::handleGenericException);
    }

    public int run(int desiredPort) {
        myServer.start(desiredPort);
        return myServer.port();
    }

    public void stop() {
        myServer.stop();
    }
    // delete or clear but do no save
    private void clear(Context ctx) throws DataAccessException {
        gameService.clear();
        ctx.status(200).json(Map.of());
    }

    // we have to post so that the user cam see
    private void register(Context ctx) throws DataAccessException {
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");
        String email    = (String) requestBody.get("email");

        var playerSession = userService.register(username, password, email);
        ctx.status(200).json(Map.of("username", playerSession.username(), "authToken", playerSession.authToken()));
    }

    // we got to see the session and post it
    private void login(Context ctx) throws DataAccessException {
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");

        var playerSession = userService.login(username, password);
        ctx.status(200).json(Map.of("username", playerSession.username(), "authToken", playerSession.authToken()));
    }

    // live session clear and delete
    private void logout(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        userService.logout(playerToken);
        ctx.status(200).json(Map.of());
    }

    // lets fetch the game
    private void listGames(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        List<GameData> allGames = gameService.listGames(playerToken);
        ctx.status(200).json(Map.of("games", allGames));
    }

    // lets post the game
    private void createGame(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String gameTitle = (String) requestBody.get("gameName");

        int newGameID = gameService.createGame(playerToken, gameTitle);
        ctx.status(200).json(Map.of("gameID", newGameID));
    }

    // where does the game join or go
    private void joinGame(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);

        String teamColor = (String) requestBody.get("playerColor");
        Object freshID = requestBody.get("gameID");

        if (freshID == null) {
            throw new DataAccessException("Error: bad request");
        }

        int chessGameID = ((Double) freshID).intValue();
        gameService.joinGame(playerToken, teamColor, chessGameID);
        ctx.status(200).json(Map.of());
    }

    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String errorMsg = e.getMessage();

        if (errorMsg.contains("bad request")) {
            ctx.status(400);
        } else if (errorMsg.contains("unauthorized")) {
            ctx.status(401);
        } else if (errorMsg.contains("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }

        ctx.json(Map.of("message", errorMsg));
    }

    private void handleGenericException(Exception e, Context ctx) {
        ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
    }
}