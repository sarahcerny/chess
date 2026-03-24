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
    private static final String whiteTile = "\u001b[47m";
    private static final String blackTile = "\u001b[40m";
    private static final String homeColor = "\u001b[31m";
    private static final String awayColor = "\u001b[34m";

    private static final String[] COL_LABELS_WHITE = {"a","b","c","d","e","f","g","h"};
    private static final String[] COL_LABELS_BLACK = {"h","g","f","e","d","c","b","a"};

    public static void drawBoard(ChessGame game, String perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boolean whiteView = perspective.equalsIgnoreCase("white");
        ChessBoard board = game.getBoard();

        String[] colLabels = whiteView ? COL_LABELS_WHITE : COL_LABELS_BLACK;
        printHeader(out, colLabels);

        int[] rowOrder = buildRowOrder(whiteView);
        int[] colOrder = buildColOrder(whiteView);

        for (int row : rowOrder) {
            out.print(row + " ");
            out.print(buildRow(board, row, colOrder, whiteView));
            out.println(" " + row);
        }

        printHeader(out, colLabels);
        out.println(resetColor);
    }

    private static int[] buildRowOrder(boolean whiteView) {
        int[] rows = new int[8];
        for (int i = 0; i < 8; i++) {
            rows[i] = whiteView ? (8 - i) : (i + 1);
        }
        return rows;
    }

    private static int[] buildColOrder(boolean whiteView) {
        int[] cols = new int[8];
        for (int i = 0; i < 8; i++) {
            cols[i] = whiteView ? (i + 1) : (8 - i);
        }
        return cols;
    }
    private static String buildRow(ChessBoard board, int row, int[] colOrder, boolean whiteView) {
        StringBuilder rowString = new StringBuilder();
        for (int col : colOrder) {
            boolean findWhiteSquare = (row + col) % 2 == 0;
            String tileColor = findWhiteSquare ? whiteTile : blackTile;
            boolean homeSide = whiteView ? row <= 2 : row >= 7;
            String textColor = homeSide ? homeColor : awayColor;
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            rowString.append(tileColor)
                    .append(textColor)
                    .append(" ")
                    .append(getLetter(piece))
                    .append(" ")
                    .append(resetColor);
        }
        return rowString.toString();
    }
    private static String getLetter(ChessPiece piece) {
        if (piece == null) return " ";
        return switch (piece.getPieceType()) {
            case KING   -> "K";
            case QUEEN  -> "Q";
            case ROOK   -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN   -> "P";
        };
    }
    private static void printHeader(PrintStream out, String[] colLabels) {
        out.print("  ");
        for (String label : colLabels) {
            out.print(" " + label + " ");
        }
        out.println();
    }

}


