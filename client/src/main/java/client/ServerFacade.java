package client;

import model.GameData;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import com.google.gson.Gson;

public class ServerFacade {

    private final String baseUrl;
    private String authToken;
    private final HttpClient client;
    private String username;
    private final Gson gson;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public String login(String username, String password) throws Exception {
        var requestObj = Map.of("username", username, "password", password);
        String requestBody = gson.toJson(requestObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }

        var result = gson.fromJson(response.body(), Map.class);
        this.authToken = (String) result.get("authToken");
        this.username = username;
        return authToken;
    }

    public String register(String username, String password, String email) throws Exception {
        var requestObj = Map.of("username", username, "password", password, "email", email);
        String requestBody = gson.toJson(requestObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }

        var result = gson.fromJson(response.body(), Map.class);
        this.authToken = (String) result.get("authToken");
        this.username = username;
        return authToken;
    }

    public void logout() throws Exception {
        if (authToken == null) {throw new IllegalStateException("Not logged in");}

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/session"))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }

        authToken = null;
        username = null;
    }

    public int createGame(String gameName) throws Exception {
        if (authToken == null) {throw new IllegalStateException("Not logged in");}

        var requestObj = Map.of("gameName", gameName);
        String requestBody = gson.toJson(requestObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }

        var result = gson.fromJson(response.body(), Map.class);
        return ((Double) result.get("gameID")).intValue();
    }

    public List<GameData> listGames() throws Exception {
        if (authToken == null) {throw new IllegalStateException("Not logged in");}

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/game"))
                .header("Authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }

        return gson.fromJson(response.body(), GamesList.class).games();
    }

    public void joinGame(int gameId, String color) throws Exception {
        if (authToken == null) {throw new IllegalStateException("Not logged in");}

        var requestObj = Map.of("playerColor", color, "gameID", gameId);
        String requestBody = gson.toJson(requestObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }
    }

    public void clearDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/db"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw handleFailure(response);
        }
    }

    public String getAuthToken() { return authToken; }
    public String getUsername() { return username; }

    private Exception handleFailure(HttpResponse<String> response) {
        try {
            var body = gson.fromJson(response.body(), Map.class);
            String message = (String) body.get("message");
            return new Exception(message);
        } catch (Exception e) {
            return new Exception("Error: " + response.statusCode());
        }
    }

    private record GamesList(List<GameData> games) {}
}