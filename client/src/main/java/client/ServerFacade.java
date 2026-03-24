package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ServerFacade {

    String serverAddress;
    String sessionToken;

    public ServerFacade(int port) {
        serverAddress = "http://localhost:" + port;
    }

    public void setTok(String token) {
        this.sessionToken = token;
    }
}