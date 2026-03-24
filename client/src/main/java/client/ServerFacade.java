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




}