package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();
    private final MessageHandler messageHandler;

    public WebSocketFacade(String serverUrl, MessageHandler messageHandler,
                           String playerToken, int roomID) throws Exception {
        this.messageHandler = messageHandler;
        String wsUrl = serverUrl.replace("http", "ws") + "/ws";
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(wsUrl));
        this.session.addMessageHandler(new jakarta.websocket.MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                messageHandler.notify(serverMessage);
            }
        });
    }