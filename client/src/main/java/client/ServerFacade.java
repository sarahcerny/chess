package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import com.google.gson.Gson;

public class ServerFacade {

    private final String serverAddress;
    private String sessionToken;
    private final HttpClient netClient;
    private String currentUser;
    private final Gson gson;

    public ServerFacade(int port) {
        this.serverAddress = "http://localhost:" + port;
        this.netClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }