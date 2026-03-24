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

}