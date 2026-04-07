package websocket.commands;

import chess.ChessMove;

public class MoveCommands extends UserGameCommand {
    private final ChessMove playerMove;

    public MoveCommands(String authToken, Integer gameID, ChessMove playerMove) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.playerMove = playerMove;
    }

    public ChessMove getPlayerMove() {
        return playerMove;
    }
}