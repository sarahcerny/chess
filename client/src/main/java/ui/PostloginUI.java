package ui;

import client.ServerFacade;
import client.ChessBoardPrinter;
import model.GameData;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PostloginUI {

    private final ServerFacade facade;
    private final Scanner scanner;
    private List<GameData> lastGames;

    public PostloginUI(ServerFacade facade, Scanner scanner) {
        this.facade = facade;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("Welcome to the Post-login menu!");
        printHelp();

        while (true) {
            System.out.print("\n[LOGGED_IN] >>> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();
            // This gathers everything after the first word
            String[] params = java.util.Arrays.copyOfRange(parts, 1, parts.length);

            switch (command) {
                case "help" -> printHelp();
                case "logout" -> { logout(); return; }
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                default -> System.out.println("Unknown command. Type 'help'.");
            }
        }
    }

    private void resetDatabase() {
        try {
            facade.clearDatabase();
            System.out.println("Database cleared! Returning to prelogin menu.");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  create <NAME>  - create a new game");
        System.out.println("  list           - list all games");
        System.out.println("  play <ID> <COLOR> - join and play (e.g., play 1 WHITE)");
        System.out.println("  observe <ID>   - watch a game");
        System.out.println("  logout         - return to main menu");
        System.out.println("  help           - show this menu");
    }

    private void logout() {
        try {
            facade.logout();
            System.out.println("Logged out successfully!");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void createGame(String[] params) {
        String name;

        if (params.length > 0) {
            name = params[0];
        } else {

            System.out.print("Enter new game name: ");
            name = scanner.nextLine().trim();
        }

        if (name.isEmpty()) {
            System.out.println("Error: game name cannot be empty");
            return;
        }
        try {
            facade.createGame(name);
            System.out.println("Game '" + name + "' created successfully!");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void listGames() {
        try {
            lastGames = facade.listGames();
            if (lastGames.isEmpty()) {
                System.out.println("No games available.");
                return;
            }
            for (int i = 0; i < lastGames.size(); i++) {
                GameData g = lastGames.get(i);

                String white = (g.whiteUsername() != null) ? g.whiteUsername() : "open";
                String black = (g.blackUsername() != null) ? g.blackUsername() : "open";

                System.out.printf("%d. %s (White: %s, Black: %s)%n",
                        i + 1, g.gameName(), white, black);
            }
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void playGame(String[] params) {
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("No games to play. Use 'list' first.");
            return;
        }

        try {
            int index;
            String color;

            // Extract "play 11 WHITE"
            if (params.length >= 2) {
                index = Integer.parseInt(params[0]) - 1;
                color = params[1].toUpperCase();
            } else {
                System.out.print("Enter game number: ");
                index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                System.out.print("Enter color (WHITE/BLACK): ");
                color = scanner.nextLine().trim().toUpperCase();
            }

            // Validate index
            if (index < 0 || index >= lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData gameData = lastGames.get(index);

            // Attempt to join via Facade
            facade.joinGame(gameData.gameID(), color);

            // If no exception was thrown, it worked!
            System.out.println("Joined successfully!");
            ChessBoardPrinter.drawBoard(gameData.game(), color);

        } catch (NumberFormatException e) {
            System.out.println("Error: Game ID must be a number.");
        } catch (Exception e) {
            // This will catch the "bad request" or "already taken" errors from the server
            String msg = e.getMessage();
            if (msg.contains("403")) {
                System.out.println("Error: That color is already taken!");
            } else if (msg.contains("400")) {
                System.out.println("Error: Bad request (Check your color spelling).");
            } else {
                printServerError(e);
            }
        }
    }

    private void observeGame(String[] params) {
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("No games to observe. Use 'list' first.");
            return;
        }

        try {
            int number;
            if (params.length > 0) {
                number = Integer.parseInt(params[0]);
            } else {
                System.out.print("Enter game number to observe: ");
                number = Integer.parseInt(scanner.nextLine().trim());
            }

            if (number < 1 || number > lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData gameData = lastGames.get(number - 1);
            System.out.println("Observing game '" + gameData.gameName() + "' (white perspective):");
            ChessBoardPrinter.drawBoard(gameData.game(), "white");

        } catch (NumberFormatException e) {
            System.out.println("Error: Please provide a valid number.");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void printServerError(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            System.out.println(msg);
        } else {
            System.out.println("Error: An unknown error occurred.");
        }
    }
}