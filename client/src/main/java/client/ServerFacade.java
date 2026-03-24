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
    private String currentUser;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverAddress = "http://localhost:" + port;
    }


    public String login(String username, String password) {
        var requestData = Map.of("username", username, "password", password);
        AuthData received = callServer("POST", "/session", requestData, AuthData.class);
        sessionToken = received.authToken();
        currentUser = username;
        return sessionToken;
    }

    public String register(String username, String password, String email) {
        UserData requestData = new UserData(username, password, email);
        AuthData received = callServer("POST", "/user", requestData, AuthData.class);
        sessionToken = received.authToken();
        currentUser = username;
        return sessionToken;
    }

    public void logout() {
        requireLogin();
        callServer("DELETE", "/session", null, null);
        sessionToken = null;
        currentUser = null;
    }


    public int createGame(String gameTitle) {
        requireLogin();
        var requestData = Map.of("gameName", gameTitle);
        JsonObject response = callServer("POST", "/game", requestData, JsonObject.class);
        return response.get("gameID").getAsInt();
    }

    public ArrayList<GameData> listGames() {
        requireLogin();
        JsonObject response = callServer("GET", "/game", null, JsonObject.class);
        JsonArray gamesArray = response.getAsJsonArray("games");
        ArrayList<GameData> gamesList = new ArrayList<>();
        for (var element : gamesArray) {
            gamesList.add(gson.fromJson(element, GameData.class));
        }
        return gamesList;
    }

    public void joinGame(int gameNumber, String teamColor) {
        requireLogin();
        var requestData = Map.of("playerColor", teamColor, "gameID", gameNumber);
        callServer("PUT", "/game", requestData, null);
    }

    public void clearDatabase() {
        callServer("DELETE", "/db", null, null);
    }


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
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new RuntimeException("Request failed with status: " + statusCode);
            }
            return readAnswer(connection, responseClass);
        } catch (RuntimeException e) {
            throw e;
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
        if (responseClass == null) return null;
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }
        if (inputStream == null) return null;
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        return gson.fromJson(streamReader, responseClass);
    }
    public void resetSession() {
        sessionToken = null;
        currentUser = null;
    }


    public String getSessionToken() { return sessionToken; }
    public String getAuthToken()    { return sessionToken; }
    public String getUsername()     { return currentUser; }
}