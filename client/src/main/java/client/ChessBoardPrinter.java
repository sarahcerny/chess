package client;

import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ChessBoardPrinter {

    private static final String RESET_COLOR = "\u001b[0m";
    private static final String WHITE_TILE = "\u001b[47m";
    private static final String BLACK_TILE = "\u001b[40m";
    private static final String HOME_COLOR = "\u001b[31m";
    private static final String AWAY_COLOR = "\u001b[34m";

    private static final String[] FILES_WHITE = {"a","b","c","d","e","f","g","h"};
    private static final String[] FILES_BLACK = {"h","g","f","e","d","c","b","a"};

    public static void drawBoard(ChessGame game, String perspective) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boolean isWhiteView = perspective.equalsIgnoreCase("white");
        ChessBoard board = game.getBoard();

        renderFiles(out, isWhiteView);

        for (int r = 0; r < 8; r++) {
            int displayRow = isWhiteView ? 8 - r : r + 1;
            out.print(displayRow + " ");

            for (int c = 0; c < 8; c++) {
                int actualRow = isWhiteView ? r + 1 : 8 - r;
                int actualCol = isWhiteView ? c + 1 : 8 - c;

                ChessPiece piece = board.getPiece(new ChessPosition(actualRow, actualCol));
                boolean isWhiteSquare = (actualRow + actualCol) % 2 == 0;

                drawSquare(out, piece, isWhiteSquare, r + 1, isWhiteView);
            }

            out.println(" " + displayRow);
        }

        renderFiles(out, isWhiteView);
        out.println(RESET_COLOR);
    }

    private static void renderFiles(PrintStream out, boolean isWhiteView) {
        out.print("  ");
        String[] files = isWhiteView ? FILES_WHITE : FILES_BLACK;
        for (String f : files) {
            out.print(" " + f + " ");
        }
        out.println();
    }

    private static void drawSquare(PrintStream out, ChessPiece piece, boolean whiteSquare,
                                   int row, boolean isWhiteView) {
        out.print(whiteSquare ? WHITE_TILE : BLACK_TILE);
        String symbol = getPieceSymbol(piece);
        boolean bottomSide = isWhiteView ? row >= 7 : row <= 2;
        String color = bottomSide ? HOME_COLOR : AWAY_COLOR;
        { out.print(color + " " + symbol + " " + RESET_COLOR); }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        switch (piece.getPieceType()) {
            case KING: return "K";
            case QUEEN: return "Q";
            case ROOK: return "R";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case PAWN: return "P";
        }
        return " ";
    }
}