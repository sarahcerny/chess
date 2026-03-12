package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.*;
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
    // using an enum instead of boolean so I can test for capture too
    public enum MoveState{
        VALID,
        INVALID,
        CAPTURE
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
        //BISHOP
        if (piece.getPieceType() == PieceType.BISHOP) {
            return this.pieceMovesDiagonal(board, myPosition, null);
        }
        //ROOK
        if (piece.getPieceType() == PieceType.ROOK) {
            Collection<ChessMove> chessMovesVertical = this.pieceMovesVertical(board, myPosition, null);
            Collection<ChessMove> chessMovesHorizontal = this.pieceMovesHorizontal(board, myPosition, null);

            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(chessMovesVertical);
            chessMoves.addAll(chessMovesHorizontal);
            return chessMoves;
        }
        //QUEEN
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
        //KING
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
        //Knight
        if (piece.getPieceType() == PieceType.KNIGHT) {
            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(this.pieceMovesL(board, myPosition, null));
            return chessMoves;
        }
        //Pawn
        if (piece.getPieceType() == PieceType.PAWN) {
            List<ChessMove> chessMoves = new ArrayList<>();
            chessMoves.addAll(this.pieceMovesPawn(board, myPosition, null));
            return chessMoves;
        }
        return List.of();
    }
    //Im creating genaric moves so that I can use these moves depending on how different pieces move.
    private Collection<ChessMove> pieceMovesDiagonal(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
//I am going to move a piece up and to the right
/** upper right  */
        int currentRowChange = 1;
        int currentColumnChange = 1;

        while (currentRowChange >= 1 && currentRowChange < 8 && currentColumnChange >= 1 && currentColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + currentRowChange, myPosition.getColumn() + currentColumnChange);

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            currentRowChange++;
            currentColumnChange++;
        }
//I am going to move a piece up and to the left
/** upper left  */
        int upperleftRowChange = 1;
        int upperleftColumnChange = 1;

        while (upperleftRowChange >= 1 && upperleftRowChange < 8 && upperleftColumnChange >= 1 && upperleftColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + upperleftRowChange, myPosition.getColumn() - upperleftColumnChange);

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            upperleftRowChange++;
            upperleftColumnChange++;
        }
//I am going to move a piece down and to the left
/** down left  */
        int downleftRowChange = 1;
        int downleftColumnChange = 1;

        while (downleftRowChange >= 1 && downleftRowChange < 8 && downleftColumnChange >= 1 && downleftColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - downleftRowChange, myPosition.getColumn() - downleftColumnChange);

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            downleftRowChange++;
            downleftColumnChange++;
        }
    // I want to move this piece down and to the right.
/** down right  */
        int downrightRowChange = 1;
        int downrightColumnChange = 1;

        while (downrightRowChange >= 1 && downrightRowChange < 8 && downrightColumnChange >= 1 && downrightColumnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - downrightRowChange, myPosition.getColumn() + downrightColumnChange);

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            downrightRowChange++;
            downrightColumnChange++;
        }
        return chessMoves;
    }
    private Collection<ChessMove> pieceMovesHorizontal(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** horizontal */
        int columnChange = 1;
        while (columnChange >= 1 && columnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + columnChange);
            // We created the enum MoveState so that we are able to use them sorta like a boolean where I can test for Validity and capture.
            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            //I have to break this for if capture is valid.
            } else {
                break;
            }
            columnChange++;
        }
        // no longer need rowChange because I can just leave it out to be simpler
        columnChange = 1;
        while (columnChange >= 1 && columnChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - columnChange);

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            columnChange++;
        }
        return chessMoves;
    }
    private Collection<ChessMove> pieceMovesVertical(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Vertical */
        int rowChange = 1;
        while (rowChange >= 1 && rowChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn());

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            rowChange++;
        }
        rowChange = 1;
        while (rowChange >= 1 && rowChange < 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - rowChange, myPosition.getColumn());

            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
            } else if (moveState == MoveState.CAPTURE) {
                chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
                break;
            } else {
                break;
            }
            rowChange++;
        }
        return chessMoves;
    }
    //this is mainly for the case of king where I can move 1 space in every direction
    private Collection<ChessMove> pieceMovesHorizontal1(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** horizontal 1 */

        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        // for my move state here I need to see if it is valid OR if there is a piece of the other color and need to CAPTURE1
        //  I dont need to have my ++ because I only want to move once
        return chessMoves;
    }
    // I am using a private case because It is so my variables are in my global
    /**
     * Vertical
     */
    private Collection<ChessMove> pieceMovesVertical1(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Vertical 1 */

        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        // I need a OR phrase because I can either move Valid or I cant move and need to capture.
        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }

    /**
     * Diagonal 1
     */
    private Collection<ChessMove> pieceMovesDiagonal1(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** Diagonal 1 */
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }

    /**
     * Moves L
     */
    private Collection<ChessMove> pieceMovesL(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        /** down Left */
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        /** down Right */
        newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        /** upper left */
        newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        /** upper right */
        newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }
        newPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
        moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID || moveState == MoveState.CAPTURE) {
            chessMoves.add(new ChessMove(myPosition, newPosition, pieceType));
        }

        return chessMoves;
    }
    private void pawnPromotion(Collection<ChessMove> chessMoves, ChessPosition myPosition, ChessPosition newPosition) {
        chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
        chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
        chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
    }
    private Collection<ChessMove> pieceMovesPawn(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            whitePawnMoves(board, myPosition, chessMoves);
        }
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            blackPawnMoves(board, myPosition, chessMoves);
        }
        return chessMoves;
    }
    private void whitePawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> chessMoves) {
        var newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID) {
            addPawnMove(chessMoves, myPosition, newPosition, 8);
            if (myPosition.getRow() == 2) {
                var doubleMove = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                if (validateMove(board, this, myPosition, doubleMove) == MoveState.VALID) {
                    addPawnMove(chessMoves, myPosition, doubleMove, 8);
                }
            }
        }
        var diagRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (validateMove(board, this, myPosition, diagRight) == MoveState.CAPTURE) {
            addPawnMove(chessMoves, myPosition, diagRight, 8);
        }
        var diagLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        if (validateMove(board, this, myPosition, diagLeft) == MoveState.CAPTURE) {
            addPawnMove(chessMoves, myPosition, diagLeft, 8);
        }
    }
    private void blackPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> chessMoves) {
        var newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        MoveState moveState = validateMove(board, this, myPosition, newPosition);
        if (moveState == MoveState.VALID) {
            addPawnMove(chessMoves, myPosition, newPosition, 1);
            if (myPosition.getRow() == 7) {
                var doubleMove = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if (validateMove(board, this, myPosition, doubleMove) == MoveState.VALID) {
                    addPawnMove(chessMoves, myPosition, doubleMove, 1);
                }
            }
        }
        var diagRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (validateMove(board, this, myPosition, diagRight) == MoveState.CAPTURE) {
            addPawnMove(chessMoves, myPosition, diagRight, 1);
        }
        var diagLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        if (validateMove(board, this, myPosition, diagLeft) == MoveState.CAPTURE) {
            addPawnMove(chessMoves, myPosition, diagLeft, 1);
        }
    }

    private void addPawnMove(Collection<ChessMove> chessMoves, ChessPosition myPosition,
                             ChessPosition newPosition, int promotionRow) {
        if (newPosition.getRow() == promotionRow) {
            pawnPromotion(chessMoves, myPosition, newPosition);
        } else {
            chessMoves.add(new ChessMove(myPosition, newPosition, null));
        }
    }
    private boolean isInBounds(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8
                && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }
    public MoveState validateMove(ChessBoard board, ChessPiece chessPiece, ChessPosition currentChessPosition, ChessPosition newChessPosition) {
        if (!isInBounds(currentChessPosition)) { return MoveState.INVALID; }
        if (!isInBounds(newChessPosition)) { return MoveState.INVALID; }

        var myPieceColor = chessPiece.getTeamColor();
        ChessPiece newChessPositionPiece = board.getPiece(newChessPosition);

        if (newChessPositionPiece != null) {
            if (newChessPositionPiece.getTeamColor() == myPieceColor) {
                return MoveState.INVALID;
            }
            return MoveState.CAPTURE;
        }

        return MoveState.VALID;
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
