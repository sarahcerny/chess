package websocket.messages;

import chess.ChessGame;

public class GameMessages extends ServerMessage {
    private final ChessGame game;

    public GameMessages(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}