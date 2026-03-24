package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverAddress;
    private String sessionToken;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverAddress = "http://localhost:" + port;
    }
    // gotta authenticate that shit.
    public String login(String username, String password) {
        AuthData received = callServer("POST", "/session",
                new UsernameAndPassword(username, password), AuthData.class);
        sessionToken = received.authToken();
        return sessionToken;
    }

    public String register(String username, String password, String email) {
        AuthData received = callServer("POST", "/user",
                new UserData(username, password, email), AuthData.class);
        sessionToken = received.authToken();
        return sessionToken;
    }

    public void logout() {
        requireLogin();
        callServer("DELETE", "/session", null, null);
        sessionToken = null;
    }
    // let the games begin

    public GameID createGame(String gameTitle) {
        requireLogin();
        return callServer("POST", "/game",
                new GameData(0, null, null, gameTitle, null), GameID.class);
    }

    public ArrayList<GameData> listGames() {
        requireLogin();
        return callServer("GET", "/game", null, GamesList.class).games();
    }

    public void joinGame(int gameNumber, String teamColor) {
        requireLogin();
        callServer("PUT", "/game",
                new ColorAndGame(ChessGame.TeamColor.valueOf(teamColor), gameNumber), null);
    }

    public void clearDatabase() {
        callServer("DELETE", "/db", null, null);
    }




}