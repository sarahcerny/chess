package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            return this.pieceMovesDiagonal(board, myPosition, null);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            Collection<ChessMove> chessMovesVertical = this.pieceMovesVertical(board, myPosition, null);
            Collection<ChessMove> chessMovesHorizontal = this.pieceMovesHorizontal(board, myPosition, null);

            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(chessMovesVertical);
            chessMoves.addAll(chessMovesHorizontal);
            return chessMoves;
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            Collection<ChessMove> chessMovesVertical = this.pieceMovesVertical(board, myPosition, null);
            Collection<ChessMove> chessMovesHorizontal = this.pieceMovesHorizontal(board, myPosition, null);
            Collection<ChessMove> chessMovesDiagonal = this.pieceMovesDiagonal(board, myPosition, null);

            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(chessMovesVertical);
            chessMoves.addAll(chessMovesHorizontal);
            chessMoves.addAll(chessMovesDiagonal);
            return chessMoves;
        }
        if (piece.getPieceType() == PieceType.KING) {
            Collection<ChessMove> chessMovesVertical1 = this.pieceMovesVertical1(board, myPosition, null);
            Collection<ChessMove> chessMovesHorizontal1 = this.pieceMovesHorizontal1(board, myPosition, null);
            Collection<ChessMove> chessMovesDiagonal1 = this.pieceMovesDiagonal1(board, myPosition, null);

            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(chessMovesVertical1);
            chessMoves.addAll(chessMovesHorizontal1);
            chessMoves.addAll(chessMovesDiagonal1);
            return chessMoves;
        }

        return List.of();
    }


    private Collection<ChessMove> pieceMovesDiagonal(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
/** upper right  */
        int currentRowChange = 1;
        int currentColumnChange = 1;

        while (currentRowChange >= 1 && currentRowChange < 8 && currentColumnChange >= 1 && currentColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + currentRowChange, myPosition.getColumn() + currentColumnChange);

            if (newPosition.validate() == true) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }

            currentRowChange++;
            currentColumnChange++;


        }
/** upper left  */
        int upperleftRowChange = 1;
        int upperleftColumnChange = 1;

        while (upperleftRowChange >= 1 && upperleftRowChange < 8 && upperleftColumnChange >= 1 && upperleftColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + upperleftRowChange, myPosition.getColumn() - upperleftColumnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            upperleftRowChange++;
            upperleftColumnChange++;
        }

/** down left  */
        int downleftRowChange = 1;
        int downleftColumnChange = 1;

        while (downleftRowChange >= 1 && downleftRowChange < 8 && downleftColumnChange >= 1 && downleftColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - downleftRowChange, myPosition.getColumn() - downleftColumnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            downleftRowChange++;
            downleftColumnChange++;
        }
/** down right  */
        int downrightRowChange = 1;
        int downrightColumnChange = 1;

        while (downrightRowChange >= 1 && downrightRowChange < 8 && downrightColumnChange >= 1 && downrightColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - downrightRowChange, myPosition.getColumn() + downrightColumnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            downrightRowChange++;
            downrightColumnChange++;
        }

        return chessMoves;
    }

    /**
     * horizontal
     */
    private Collection<ChessMove> pieceMovesHorizontal(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** horizontal */
        int rowChange = 0;
        int columnChange = 1;
        while (columnChange >= 1 && columnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn() + columnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            columnChange++;
        }

        rowChange = 0;
        columnChange = 1;
        while (columnChange >= 1 && columnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn() - columnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            columnChange++;
        }
        return chessMoves;
    }

    /**
     * Vertical
     */
    private Collection<ChessMove> pieceMovesVertical(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Vertical */
        int rowChange = 1;
        int columnChange = 0;
        while (rowChange >= 1 && rowChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn() + columnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            rowChange++;
        }
        rowChange = 1;
        columnChange = 0;
        while (rowChange >= 1 && rowChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - rowChange, myPosition.getColumn() + columnChange);

            if (newPosition.validate()) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else {
                break;
            }
            rowChange++;
        }
        return chessMoves;
    }
    private Collection<ChessMove> pieceMovesHorizontal1 (ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** horizontal 1 */

        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }

    /**
     * Vertical
     */
    private Collection<ChessMove> pieceMovesVertical1 (ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Vertical 1 */

        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }
    /** Diagonal 1 */
    private Collection<ChessMove> pieceMovesDiagonal1(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Diagonal 1 */
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        if (newPosition.validate()) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
