package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessages;
import websocket.messages.NotificationMessage;
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
                ServerMessage base = new Gson().fromJson(message, ServerMessage.class);
                ServerMessage serverMessage;
                switch (base.getServerMessageType()) {
                    case LOAD_GAME -> serverMessage = new Gson().fromJson(message, GameMessages.class);
                    case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                    case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                    default -> serverMessage = base;
                }
                messageHandler.notify(serverMessage);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
    }

    public void sendMessage(String msg) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (Exception e) {
            System.out.println("Failed to send: " + e.getMessage());
        }
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason.getReasonPhrase() +
                " code=" + reason.getCloseCode());
    }

    @Override
    public void onError(Session session, Throwable error) {
        System.out.println("WebSocket error: " + error.getMessage());
    }
}
