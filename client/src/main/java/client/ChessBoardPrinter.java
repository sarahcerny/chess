package client;

import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ChessBoardPrinter {

    private static final int SQUARE_SIZE = 3;

    private static final String resetColor = "\u001b[0m";
    private static final String whiteTile = "\u001b[47m"; // white squares
    private static final String blackTile = "\u001b[40m"; // black squares
    private static final String homeColor = "\u001b[31m";  // bottom of white perspective
    private static final String awayColor = "\u001b[34m"; // bottom of black perspective / opponent side

    private static final String[] COL_LABELS_WHITE = {"a","b","c","d","e","f","g","h"};
    private static final String[] COL_LABELS_BLACK = {"h","g","f","e","d","c","b","a"};

    public static void drawBoard(ChessGame game, String perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boolean whitePerspective = perspective.equalsIgnoreCase("white");
        ChessBoard board = game.getBoard();

        printHeader(out, whitePerspective);

        for (int row = 1; row <= 8; row++) {
            int displayRow = whitePerspective ? 9 - row : row;
            out.print(displayRow + " ");

            for (int col = 1; col <= 8; col++) {
                int actualRow = whitePerspective ? row : 9 - row;
                int actualCol = whitePerspective ? col : 9 - col;

                ChessPosition pos = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(pos);

                boolean isWhiteSquare = (actualRow + actualCol) % 2 == 0;

                printSquare(out, piece, isWhiteSquare, row, whitePerspective);
            }

            out.println(" " + displayRow);
        }

        printHeader(out, whitePerspective);
        out.println(resetColor);
    }

    private static void printHeader(PrintStream out, boolean whitePerspective) {
        out.print("  ");
        String[] colLabels = whitePerspective ? COL_LABELS_WHITE : COL_LABELS_BLACK;
        for (String label : colLabels) {
            out.print(" " + label + " ");
        }
        out.println();
    }
    // print square

    private static void printSquare(PrintStream out, ChessPiece piece, boolean isWhiteSquare,
                                    int row, boolean whitePerspective) {
        out.print(isWhiteSquare ? whiteTile : blackTile);
        String symbol = " ";
        if (piece != null) {
            switch (piece.getPieceType()) {
                case KING -> symbol = "K";
                case QUEEN -> symbol = "Q";
                case ROOK -> symbol = "R";
                case BISHOP -> symbol = "B";
                case KNIGHT -> symbol = "N";
                case PAWN -> symbol = "P";
            }
        }
        boolean bottomSide = whitePerspective ? row >= 7 : row <= 2;
        String textColor = bottomSide ? homeColor : awayColor;

        out.print(textColor + " " + symbol + " " + resetColor);
    }
}