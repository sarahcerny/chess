package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessages;
import websocket.messages.NotificationMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    // looks all sessions by session key to track
    private final Map<String, WsContext> playerSessions = new ConcurrentHashMap<>();
    // tracks which sessions belong to which game room which i need
    private final Map<Integer, Set<String>> roomSessions = new ConcurrentHashMap<>();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void onConnect(WsContext session) {
        playerSessions.put(session.sessionId(), session);
    }

    public void onClose(WsContext session) {
        playerSessions.remove(session.sessionId());
        roomSessions.values().forEach(set -> set.remove(session.sessionId()));
    }

    public void onMessage(WsMessageContext session) {
        try {
            UserGameCommand gameCommand = gson.fromJson(session.message(), UserGameCommand.class);
            switch (gameCommand.getCommandType()) {
                case CONNECT  -> handleConnect(session, gameCommand);
                case MAKE_MOVE -> handleMakeMove(session, gson.fromJson(session.message(), MoveCommands.class));
                case LEAVE    -> handleLeave(session, gameCommand);
                case RESIGN   -> handleResign(session, gameCommand);
            }
        } catch (Exception e) {
            notifyPlayer(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }}