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