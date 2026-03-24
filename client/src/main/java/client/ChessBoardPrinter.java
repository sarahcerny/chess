package client;

import chess.*;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    private static final String EMPTY = "   ";
    public static void drawBoard(ChessGame game, String teamColor) {
        ChessBoard board = game.getBoard();
        boolean isWhite = teamColor.equalsIgnoreCase("WHITE");
        System.out.println();
        printColumnLabels(isWhite);
        for (int row = 0; row < 8; row++) {
            int actualRow = isWhite ? (8 - row) : (row + 1);
            System.out.print(SET_TEXT_COLOR_WHITE + " " + actualRow + " ");
            for (int col = 0; col < 8; col++) {
                int actualCol = isWhite ? (col + 1) : (8 - col);
                boolean isLightSquare = (actualRow + actualCol) % 2 == 0;
                String bgColor = isLightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                ChessPosition pos = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(pos);
                System.out.print(bgColor + getPieceDisplay(piece));
            }
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + actualRow + " ");
            System.out.println();
        }
    }


