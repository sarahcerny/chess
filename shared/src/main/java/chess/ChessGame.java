package chess;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.beans.PropertyWrapper;

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
        var validMoves = new ArrayList<ChessMove>();

        ChessPiece piece = this.getBoard().getPiece(startPosition);
        if(piece == null){
            return null;
        }
        var potentialMoves = piece.pieceMoves(this.getBoard(), startPosition);
        for(ChessMove currentMove : potentialMoves) {
            if(validBoard(piece, currentMove)){
                validMoves.add(currentMove);
            }
        }

        return validMoves;
    }

    private boolean validBoard(ChessPiece piece, ChessMove move) {
        var potentialBoard = cloneBoard(this.getBoard());
        potentialBoard.addPiece(move.getStartPosition(),null);
        potentialBoard.addPiece(move.getEndPosition(), clonePiece(piece));

        var isInCheck = this.isBoardInCheck(potentialBoard, piece.getTeamColor());
        if (isInCheck) {
            return false;
        }
        return true;
    }

    private ChessPosition findKingPosition(ChessBoard board, TeamColor color){
        ChessPosition kingPosition = null;
        for(int i = 1; i <=8; i++ ){
            for(int j = 1; j <=8; j++ ) {
                var position = new ChessPosition(i, j);
                ChessPiece potentialPiece = board.getPiece(position);
                if (potentialPiece != null) {
                    if(potentialPiece.getPieceType() == ChessPiece.PieceType.KING
                            && potentialPiece.getTeamColor() == color) {
                        kingPosition = new ChessPosition(i,j);

                    }
                }
            }
        }
        return kingPosition;
    }
    private ChessBoard cloneBoard(ChessBoard thatBoard) {
        var potentialBoard = new ChessBoard();

        for(int i = 1; i <=8; i++ ){
            for(int j = 1; j <=8; j++ ) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = thatBoard.getPiece(position);
                if (piece != null) {
                    var potentialPiece = clonePiece(piece);
                    potentialBoard.addPiece(position, piece);
                }
            }
        }

        return potentialBoard;
    }
    private ChessPiece clonePiece(ChessPiece thatPiece){
        var potentialPiece = new ChessPiece(thatPiece.getTeamColor(), thatPiece.getPieceType());
        return potentialPiece;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var validMoves = validMoves(move.getStartPosition());
        var board = this.getBoard();
        var piece = board.getPiece(move.getStartPosition());
        
        if(validMoves != null
                && isInValidMove(move, validMoves)
                && piece.getTeamColor() == getTeamTurn()) {
            board.addPiece(move.getStartPosition(), null);
            ChessPiece newPiece = piece;
            if(move.getPromotionPiece() != null){
                newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), newPiece);
            var otherTeamColor = getOtherTeamColor(piece.getTeamColor());
            
            this.setTeamTurn(otherTeamColor);
        } else{
            throw new InvalidMoveException();
        }
    }
    
    private TeamColor getOtherTeamColor(TeamColor myTeamColor){
        if(myTeamColor == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;

    }
    private boolean isInValidMove(ChessMove move, Collection<ChessMove> validMoves) {
        if (move == null || validMoves == null) {
            return false;
        }

        for(ChessMove currentMove: validMoves){
            if(movesAreEqual(move, currentMove) ){
                return true;
            }
        }
        return false;
    }
    private boolean movesAreEqual(ChessMove move1, ChessMove move2){

        if (move1.getStartPosition().getRow() == move2.getStartPosition().getRow() &&
                move1.getStartPosition().getColumn() == move2.getStartPosition().getColumn() &&
                move1.getEndPosition().getRow() == move2.getEndPosition().getRow() &&
                move1.getEndPosition().getColumn() == move2.getEndPosition().getColumn() &&
                move1.getPromotionPiece() == move2.getPromotionPiece()) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var board = this.getBoard();

        return isBoardInCheck(board, teamColor);
    }

    private boolean isBoardInCheck(ChessBoard board, TeamColor teamColor){
        var kingPosition = findKingPosition(board, teamColor);
        for(int i = 1; i <=8; i++ ){
            for(int j = 1; j <=8; j++ ) {
                var position = new ChessPosition(i, j);
                ChessPiece potentialPiece = board.getPiece(position);
                if (potentialPiece != null) {
                    if (potentialPiece.getTeamColor() != teamColor) {
                        var potentialPosition = new ChessPosition(i,j);
                        var potentialPieceMoves = potentialPiece.pieceMoves(board, potentialPosition);
                        for(ChessMove currentMove : potentialPieceMoves) {
                            if(currentMove.getEndPosition().getRow() == kingPosition.getRow()
                                    && currentMove.getEndPosition().getColumn() == kingPosition.getColumn()){
                                return true;
                            }
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
        var board = this.getBoard();
        for(int i = 1; i <=8; i++ ){
            for(int j = 1; j <=8; j++ ) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    var validMoves = this.validMoves(position);
                    if(validMoves == null || !validMoves.isEmpty() ){
                        return false;
                    }
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
        if (isInCheck(teamColor)) {
            return false;
        }
        var board = this.getBoard();
        for(int i = 1; i <=8; i++ ){
            for(int j = 1; j <=8; j++ ) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    var validMoves = this.validMoves(position);
                    if(validMoves == null || !validMoves.isEmpty() ){
                        return false;
                    }
                }
            }
        }

        return true;
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
