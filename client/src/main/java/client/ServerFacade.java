package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class ServerFacade {

    private final String serverAddress;
    private String sessionToken;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverAddress = "http://localhost:" + port;
    }

    public String login(String username, String password) {
        var requestData = Map.of("username", username, "password", password);
        AuthData received = callServer("POST", "/session", requestData, AuthData.class);
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

    public int createGame(String gameTitle) {
        requireLogin();
        var requestData = Map.of("gameName", gameTitle);
        JsonObject response = callServer("POST", "/game", requestData, JsonObject.class);
        return response.get("gameID").getAsInt();
    }

    public ArrayList<GameData> listGames() {
        requireLogin();
        return callServer("GET", "/game", null, GamesList.class).games();
    }

    public void joinGame(int gameNumber, String teamColor) {
        requireLogin();
        var requestData = Map.of("playerColor", teamColor, "gameID", gameNumber);
        callServer("PUT", "/game", requestData, null);
    }

    public void clearDatabase() {
        callServer("DELETE", "/db", null, null);
    }
    // help a sister out please

        private void requireLogin() {
            if (sessionToken == null) {
                throw new RuntimeException("You must be logged in first.");
            }
        }

    private <T> T callServer(String method, String endpoint, Object requestData, Class<T> responseClass) {
        try {
            HttpURLConnection connection = (HttpURLConnection)
                    new URI(serverAddress + endpoint).toURL().openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("authorization", sessionToken);
            writeData(requestData, connection);
            connection.connect();
            connection.getResponseCode();
            return readAnswer(connection, responseClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeData(Object requestData, HttpURLConnection connection) throws IOException {
        if (requestData != null) {
            connection.addRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(gson.toJson(requestData).getBytes());
        }
    }


    private <T> T readAnswer(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        if (responseClass != null && connection.getContentLength() < 0) {
            InputStream inputStream = connection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            return gson.fromJson(streamReader, responseClass);
        }
        return null;
    }
    public String getSessionToken() { return sessionToken; }
}



