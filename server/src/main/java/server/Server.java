package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.GameData;
import service.GameService;
import service.UserService;

import java.util.List;
import java.util.Map;

public class Server {
// server runs the whole system fr
    private final Javalin myServer;
    private final UserService userService;
    private final GameService gameService;
    //gson is going to convert json stuff
    private final Gson gsonMap = new Gson();

    public Server() {
        // one dataAccess shared across both services so they collab
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess, dataAccess);
        gameService = new GameService(dataAccess, dataAccess, dataAccess);
        // spin up javalin and plug in gson so it knows how to handle json when she comes ur way
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
        // hooks everything up lets gooo yaya
        registerRoutes();
        registerExceptionHandlers();
    }
    // all 7 endpoints live here so we got to keep it neat with the routes
    private void registerRoutes() {
        myServer.delete("/db",      this::clear);
        myServer.post("/user",      this::register);
        myServer.post("/session",   this::login);
        myServer.delete("/session", this::logout);
        myServer.get("/game",       this::listGames);
        myServer.post("/game",      this::createGame);
        myServer.put("/game",       this::joinGame);
    }

    // catches errors so i dont have to try/catch in every single method
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
    // makes it fresh clean start of day type beat
    private void clear(Context ctx) throws DataAccessException {
        gameService.clear();
        ctx.status(200).json(Map.of());
    }

    // new player signing up so we get their contact info form request body
    private void register(Context ctx) throws DataAccessException {
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");
        String email    = (String) requestBody.get("email");


        // yay new player send back their token so they can do stuff
        var playerSession = userService.register(username, password, email);
        ctx.status(200).json(Map.of("username", playerSession.username(), "authToken", playerSession.authToken()));
    }

    // player coming back check their password and give them a fresh token
    private void login(Context ctx) throws DataAccessException {
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");

        var playerSession = userService.login(username, password);
        ctx.status(200).json(Map.of("username", playerSession.username(), "authToken", playerSession.authToken()));
    }

    // peace out kill their token bc we cant use it no more gang
    private void logout(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        userService.logout(playerToken);
        ctx.status(200).json(Map.of());
    }

    // pull the token from header first to make sure their valid fr
    private void listGames(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        List<GameData> allGames = gameService.listGames(playerToken);
        ctx.status(200).json(Map.of("games", allGames));
    }

    // make a brand new chess game and send back the id
    private void createGame(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);
        String gameTitle = (String) requestBody.get("gameName");

        int newGameID = gameService.createGame(playerToken, gameTitle);
        ctx.status(200).json(Map.of("gameID", newGameID));
    }

    // player picking a side - WHITE or BLACK like the mj song
    private void joinGame(Context ctx) throws DataAccessException {
        String playerToken = ctx.header("authorization");
        var requestBody = gsonMap.fromJson(ctx.body(), Map.class);

        String teamColor = (String) requestBody.get("playerColor");
        Object freshID = requestBody.get("gameID");

        if (freshID == null) {
            throw new DataAccessException("Error: bad request");
        }
        // gson is weird and reads numbers as double so gotta handle that stuff
        int chessGameID = ((Double) freshID).intValue();
        gameService.joinGame(playerToken, teamColor, chessGameID);
        ctx.status(200).json(Map.of());
    }

    // figure out which error code to send back based on what went wrong
    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String errorMsg = e.getMessage();
        // lets match/ship the messages http
        if (errorMsg.contains("bad request")) {
            ctx.status(400);
        } else if (errorMsg.contains("unauthorized")) {
            ctx.status(401);
        } else if (errorMsg.contains("already taken")) {
            ctx.status(403);
        } else {
            // something broke that i didnt expect lol
            ctx.status(500);
        }

        ctx.json(Map.of("message", errorMsg));
    }
    // catch all for anything random that will break
    private void handleGenericException(Exception e, Context ctx) {
        ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
    }
}