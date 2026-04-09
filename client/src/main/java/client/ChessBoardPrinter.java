package client;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardPrinter {

    private static final String RESET_COLOR  = "\u001b[0m";
    private static final String WHITE_TILE   = "\u001b[48;5;255m";;
    private static final String BLACK_TILE   = "\u001b[40m";
    private static final String GREEN_LIGHT = "\u001b[48;5;114m";
    private static final String GREEN_DARK  = "\u001b[48;5;64m";
    private static final String YELLOW_TILE  = "\u001b[43m";
    private static final String HOME_COLOR   = "\u001b[31m";
    private static final String AWAY_COLOR   = "\u001b[34m";

    private static final String[] FILES_WHITE = {"a","b","c","d","e","f","g","h"};
    private static final String[] FILES_BLACK = {"h","g","f","e","d","c","b","a"};

    public static void drawBoard(ChessGame game, String perspective) {
        drawBoard(game, perspective, null, null);
    }

    public static void drawBoard(ChessGame game, String perspective,
                                 ChessPosition selected, Collection<ChessMove> legalMoves) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boolean isWhiteView = perspective.equalsIgnoreCase("white");
        ChessBoard board = game.getBoard();

        Set<ChessPosition> highlights = new HashSet<>();
        if (legalMoves != null) {
            for (ChessMove m : legalMoves) { highlights.add(m.getEndPosition()); }
        }

        renderFiles(out, isWhiteView);

        for (int r = 0; r < 8; r++) {
            int displayRow = isWhiteView ? 8 - r : r + 1;
            out.print(displayRow + " ");

            for (int c = 0; c < 8; c++) {
                int actualRow = isWhiteView ? 8 - r : r + 1;
                int actualCol = isWhiteView ? c + 1 : 8 - c;

                ChessPosition pos = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(pos);
                boolean isWhiteSquare = (actualRow + actualCol) % 2 == 0;

                boolean isSelected  = selected != null && pos.equals(selected);
                boolean isHighlight = highlights.contains(pos);

                drawSquare(out, piece, isWhiteSquare, isSelected, isHighlight);
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
                                   boolean isSelected, boolean isHighlight) {
        String bg;
        if (isSelected) {
            bg = YELLOW_TILE;
        } else if (isHighlight) {
            bg = whiteSquare ? GREEN_LIGHT : GREEN_DARK;  // different shade per square color
        } else {
            bg = whiteSquare ? WHITE_TILE : BLACK_TILE;
        }

        out.print(bg);

        String color;
        if (isSelected) {
            color = "\u001b[30m"; // black text on yellow
        } else if (isHighlight) {
            color = "\u001b[30m"; // black text on green
        } else {
            color = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? HOME_COLOR : AWAY_COLOR;
        }

        String symbol = getPieceSymbol(piece);
        out.print(color + " " + symbol + " " + RESET_COLOR);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        switch (piece.getPieceType()) {
            case KING:   return "K";
            case QUEEN:  return "Q";
            case ROOK:   return "R";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case PAWN:   return "P";
        }
        return " ";
    }
}