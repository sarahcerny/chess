package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    private ChessBoard testBoard;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return turn == chessGame.turn && Objects.equals(board, chessGame.board) && Objects.equals(testBoard, chessGame.testBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board, testBoard);
    }

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board =  new ChessBoard();
        this.board.resetBoard();


    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null) {
            return null;
        }
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                ChessPosition currentPosition = new ChessPosition(i +1, j + 1);
                ChessPiece piece = board.getPiece(currentPosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return currentPosition;
                }
            }
        }

        return null;
    }
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition myKing = findKingPosition(teamColor);
        if(myKing == null) {
            return false;
        }
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {

                ChessPosition currentPosition = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() != teamColor) {

                    Collection<ChessMove> currrentPieceMoves =
                            piece.pieceMoves(board, currentPosition);

                    for(ChessMove currrentMove : currrentPieceMoves) {
                        if(currrentMove.getEndPosition().equals(myKing)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

        /**
         * Determines if the given team is in checkmate
         *
         * @param teamColor which team to check for checkmate
         * @return True if the specified team is in checkmate
         */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition currentPosition = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(currentPosition);
                    if (moves != null && !moves.isEmpty()) return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
            if (!isInCheck(teamColor)) {

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {

                        ChessPosition currentPosition = new ChessPosition(i + 1, j + 1);
                        ChessPiece piece = board.getPiece(currentPosition);

                        if (piece != null && piece.getTeamColor() == teamColor) {

                            Collection<ChessMove> moves = validMoves(currentPosition);

                            if (moves != null && !moves.isEmpty()) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

}
