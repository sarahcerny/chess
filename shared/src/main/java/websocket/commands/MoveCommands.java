package websocket.commands;

import chess.ChessMove;

public class MoveCommands extends UserGameCommand {
    private final ChessMove move;

    public MoveCommands(String authToken, Integer gameID, ChessMove playerMove) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = playerMove;
    }

    public ChessMove getPlayerMove() {
        return move;
    }
}