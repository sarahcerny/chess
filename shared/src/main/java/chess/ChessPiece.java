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
    //instead of a boolean which i once had I am going to create a enum of valid invalid and Capture because I want it to test for capture if there is a peice.
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
// now I am creating a piece Moves Horizontal for pieces like rook and queen
    /**
     * horizontal
     */
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
    // creating this function so that when I have a piece that checks for vertical
    /**
     * Vertical
     */
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

    private Collection<ChessMove> pieceMovesPawn(ChessBoard board, ChessPosition myPosition, PieceType pieceType) {
        Collection<ChessMove> chessMoves = new ArrayList<ChessMove>();

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            //I am white and I can move 1 or 2 spaces vertically
            // when white reaches row 8 it becomes a QUEEN.
            var newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                if (newPosition.getRow() == 8) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }

                if (myPosition.getRow() == 2) {
                    newPosition = new ChessPosition(myPosition.getRow() +2 , myPosition.getColumn());
                    moveState = validateMove(board, this, myPosition, newPosition);
                    if (moveState == MoveState.VALID) {
                        if (newPosition.getRow() == 8) {
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            chessMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }

            // I can also capture diagonal
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.CAPTURE) {
                if (newPosition.getRow() == 8) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.CAPTURE) {
                if (newPosition.getRow() == 8) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            //I am Black and I can move 1 or 2 spaces vertically
            var newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            MoveState moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.VALID) {
                if (newPosition.getRow() == 1) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                if (myPosition.getRow() == 7) {
                    newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                    moveState = validateMove(board, this, myPosition, newPosition);
                    if (moveState == MoveState.VALID) {
                        if (newPosition.getRow() == 1) {
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            chessMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }
            // I can also capture diagonal
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.CAPTURE) {
                PieceType promotionType = null;
                if (newPosition.getRow() == 1) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            moveState = validateMove(board, this, myPosition, newPosition);
            if (moveState == MoveState.CAPTURE) {
                if (newPosition.getRow() == 1) {
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else {
                    chessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        return chessMoves;
    }

        public MoveState validateMove(ChessBoard board, ChessPiece chessPiece, ChessPosition currentChessPosition, ChessPosition newChessPosition) {
        // Is this a valid currentChessPosition on the board
        if (currentChessPosition.getRow() > 8) {
            return MoveState.INVALID;
        }

        if (currentChessPosition.getRow() < 1) {
            return MoveState.INVALID;
        }

        if (currentChessPosition.getColumn() > 8) {
            return MoveState.INVALID;
        }

        if (currentChessPosition.getColumn() < 1) {
            return MoveState.INVALID;
        }

        // Is this a valid newChessPosition on the board
        if (newChessPosition.getRow() > 8) {
            return MoveState.INVALID;
        }

        if (newChessPosition.getRow() < 1) {
            return MoveState.INVALID;
        }

        if (newChessPosition.getColumn() > 8) {
            return MoveState.INVALID;
        }

        if (newChessPosition.getColumn() < 1) {
            return MoveState.INVALID;
        }

        var myPieceColor = chessPiece.getTeamColor();
        ChessPiece newChessPositionPiece = board.getPiece(newChessPosition);

        // If there is nothing on the space.  The space is a valid move
        // If something is there we need to do further validation
        if (newChessPositionPiece != null) {
            // If the spot is owned by my color.  Not a valid move
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
