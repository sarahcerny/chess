package client;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PostloginUI {

    private final ServerFacade serverFacade;
    private final Scanner input;
    private ArrayList<GameData> gamesList;
    private final Map<Integer, GameData> gameMap = new HashMap<>();

    public PostloginUI(ServerFacade serverFacade, Scanner input) {
        this.serverFacade = serverFacade;
        this.input = input;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_YELLOW + "Logged in! Type 'help' to see your options.");
        while (true) {
            System.out.print(SET_TEXT_COLOR_WHITE + "[LOGGED_IN] >>> ");
            String userAction = input.nextLine().trim();
            var inputParts = userAction.toLowerCase().split(" ");
            var holdInput = (inputParts.length > 0) ? inputParts[0] : "help";
            var args = Arrays.copyOfRange(inputParts, 1, inputParts.length);

            String result = switch (holdInput) {
                case "help"    -> helpMenu();
                case "logout"  -> {
                    logout();
                    yield "logout";
                }
                case "create"  -> makeGame(args);
                case "list"    -> getGames();
                case "play"    -> playChess(args);
                case "observe" -> viewGame(args);
                case "quit"    -> {
                    System.out.println(SET_TEXT_COLOR_RED + "Leaving chess. Goodbye!");
                    yield "quit";
                }
                default -> SET_TEXT_COLOR_RED + "Unknown command. Type 'help' to see available commands.";
            };

            System.out.println(result);
            if (result.equals("logout") || result.equals("quit")) return;
        }
    }

    private String helpMenu() {
        return SET_TEXT_COLOR_YELLOW +
                "  create <NAME>         - create a new game\n" +
                "  list                  - list all games\n" +
                "  play <NUM> <COLOR>    - join a game as white or black\n" +
                "  observe <NUM>         - watch a game\n" +
                "  logout                - log out\n" +
                "  quit                  - exit the program\n" +
                "  help                  - show this menu";
    }

    private void logout() {
        try {
            serverFacade.logout();
            System.out.println(SET_TEXT_COLOR_GREEN + "Logged out successfully!");
        } catch (Exception e) {
            System.out.println(handleError(e));
        }
    }

    private String makeGame(String[] args) {
        if (args.length < 1) {
            return SET_TEXT_COLOR_RED + "Missing game name. Usage: create <NAME>";
        }
        if (args.length > 1) {
            return SET_TEXT_COLOR_RED + "Too many arguments. Usage: create <NAME>";
        }
        try {
            String gameTitle = args[0];
            serverFacade.createGame(gameTitle);
            return SET_TEXT_COLOR_GREEN + "Game '" + gameTitle + "' created!";
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private String getGames() {
        try {
            gamesList = serverFacade.listGames();
            gameMap.clear();
            if (gamesList.isEmpty()) {
                return SET_TEXT_COLOR_YELLOW + "No games available. Create one with 'create <NAME>'!";
            }
            StringBuilder display = new StringBuilder(SET_TEXT_COLOR_BLUE + "Current games:\n");
            for (int i = 0; i < gamesList.size(); i++) {
                GameData currentGame = gamesList.get(i);
                gameMap.put(i + 1, currentGame);
                display.append(String.format("  %d. %s | White: %s | Black: %s%n",
                        i + 1,
                        currentGame.gameName(),
                        currentGame.whiteUsername() != null ? currentGame.whiteUsername() : "open",
                        currentGame.blackUsername() != null ? currentGame.blackUsername() : "open"));
            }
            return display.toString();
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private String playChess(String[] args) {
        if (args.length < 2) {
            return SET_TEXT_COLOR_RED + "Missing arguments. Usage: play <NUM> <COLOR>";
        }
        if (args.length > 2) {
            return SET_TEXT_COLOR_RED + "Too many arguments. Usage: play <NUM> <COLOR>";
        }
        if (gameMap.isEmpty()) {
            return SET_TEXT_COLOR_RED + "No games loaded. Type 'list' first.";
        }
        try {
            int gameNum = Integer.parseInt(args[0]);
            String teamColor = args[1].toUpperCase();

            if (!teamColor.equals("WHITE") && !teamColor.equals("BLACK")) {
                return SET_TEXT_COLOR_RED + "Invalid color. Choose 'white' or 'black'.";
            }
            if (!gameMap.containsKey(gameNum)) {
                return SET_TEXT_COLOR_RED + "Invalid game number. Type 'list' to see available games.";
            }

            GameData currentGame = gameMap.get(gameNum);
            serverFacade.joinGame(currentGame.gameID(), teamColor);
            System.out.println(SET_TEXT_COLOR_GREEN + "Joined '" + currentGame.gameName() + "' as " + teamColor + "!");
            ChessBoardPrinter.drawBoard(currentGame.game(), teamColor);
            return SET_TEXT_COLOR_YELLOW + "Returned to post-login menu.";
        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED + "Invalid game number. Please enter a number.";
        } catch (Exception e) {
            return handleError(e);
        }
    }


}