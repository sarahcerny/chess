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




}


