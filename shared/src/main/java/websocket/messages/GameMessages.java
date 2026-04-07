package websocket.messages;

import chess.ChessGame;

public class GameMessages extends ServerMessage {
    private final ChessGame gameState;

    public GameMessages(ChessGame gameState) {
        super(ServerMessageType.LOAD_GAME);
        this.gameState = gameState;
    }

    public ChessGame getGameState() {
        return gameState;
    }
}