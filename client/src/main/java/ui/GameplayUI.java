package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.MoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessages;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class GameplayUI implements MessageHandler {

    private WebSocketFacade gameSocket;
    private ChessBoard gameState;
    private final Scanner userInput = new Scanner(System.in);
    private final ChessGame.TeamColor playerColor;
    private final int roomID;
    private final String playerToken;
    private final Gson gson = new Gson();
    private boolean inGame = true;

    public GameplayUI(String serverUrl, String playerToken, int roomID, ChessGame.TeamColor playerColor) throws Exception {
        this.playerToken = playerToken;
        this.roomID = roomID;
        this.playerColor = playerColor;
        this.gameSocket = new WebSocketFacade(serverUrl, this, playerToken, roomID);
        // send connect command
        UserGameCommand connectCmd = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT, playerToken, roomID);
        gameSocket.sendMessage(gson.toJson(connectCmd));
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                gameState = message.getGame();  // getGame() returns ChessBoard
                printBoard();
                printPrompt();
            }
            case NOTIFICATION -> {
                NotificationMessage note = gson.fromJson(gson.toJson(message), NotificationMessage.class);
                System.out.println("\n" + note.getMessage());
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage err = gson.fromJson(gson.toJson(message), ErrorMessage.class);
                System.out.println("\nError: " + err.getErrorMessage());
                printPrompt();
            }
        }
    }

    public void start() {
        helpMenu();
        while (inGame) {
            printPrompt();
            String line = userInput.nextLine().trim();
            handleInput(line);
        }
    }

    private void handleInput(String line) {
        var parts = line.toLowerCase().split(" ");
        var cmd = parts.length > 0 ? parts[0] : "help";
        var args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (cmd) {
            case "help" -> helpMenu();
            case "redraw" -> printBoard();
            case "move" -> makeMove(args);
            case "resign" -> resign();
            case "leave" -> leave();
            case "highlight" -> highlightMoves(args);
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
    }

    private void makeMove(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: move <from> <to>  (e.g. move e2 e4)");
            return;
        }
        ChessPosition from = parsePosition(args[0]);
        ChessPosition to = parsePosition(args[1]);
        if (from == null || to == null) {
            System.out.println("Invalid position. Use format like e2 or a1.");
            return;
        }
        ChessMove playerMove = new ChessMove(from, to, null);
        MoveCommands moveCmd = new MoveCommands(playerToken, roomID, playerMove);
        gameSocket.sendMessage(gson.toJson(moveCmd));
    }

    private void resign() {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = userInput.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            UserGameCommand resignCmd = new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN, playerToken, roomID);
            gameSocket.sendMessage(gson.toJson(resignCmd));
        } else {
            System.out.println("Resign cancelled.");
        }
    }

    private void leave() {
        UserGameCommand leaveCmd = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE, playerToken, roomID);
        gameSocket.sendMessage(gson.toJson(leaveCmd));
        inGame = false;
        System.out.println("You left the game.");
    }

    private void highlightMoves(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: highlight <position>  (e.g. highlight e2)");
            return;
        }
        if (gameState == null) {
            System.out.println("No game loaded yet.");
            return;
        }
        ChessPosition pos = parsePosition(args[0]);
        if (pos == null) {
            System.out.println("Invalid position.");
            return;
        }
        Collection<ChessMove> moves = gameState.validMoves(pos);
        if (moves == null || moves.isEmpty()) {
            System.out.println("No legal moves for that piece.");
            return;
        }
        client.ChessBoardPrinter.drawBoard(gameState, playerColor.name());
    }

    private void printBoard() {
        if (gameState == null) {
            System.out.println("No game loaded yet.");
            return;
        }
        client.ChessBoardPrinter.drawBoard(gameState, playerColor.name());
    }

    private void helpMenu() {
        System.out.println(
                "Commands:\n" +
                        "  move <from> <to>  - make a move (e.g. move e2 e4)\n" +
                        "  highlight <pos>   - show legal moves for a piece\n" +
                        "  redraw            - redraw the board\n" +
                        "  resign            - forfeit the game\n" +
                        "  leave             - leave the game\n" +
                        "  help              - show this menu");
    }

    private void printPrompt() {
        System.out.print("\n[IN GAME] >>> ");
    }

    private ChessPosition parsePosition(String input) {
        if (input == null || input.length() < 2) { return null; }
        char colChar = input.charAt(0);
        char rowChar = input.charAt(1);
        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);
        if (col < 1 || col > 8 || row < 1 || row > 8) { return null; }
        return new ChessPosition(row, col);
    }
}