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
        System.out.println("\nWelcome to the Post-Login menu!");
        printHelp();

        while (true) {
            System.out.print("\n[LOGGED_IN] >>> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            var parts = line.split("\\s+"); // Split by any whitespace
            String command = parts[0].toLowerCase();
            String[] params = Arrays.copyOfRange(parts, 1, parts.length);

            switch (command) {
                case "help" -> printHelp();
                case "logout" -> {
                    logout();
                    return;
                }
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Unknown command: " + command + ". Type 'help' for options.");
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
        System.out.println("  help         - show this help message");
        System.out.println("  logout       - log out and return to prelogin menu");
        System.out.println("  create game  - create a new game");
        System.out.println("  list games   - list all existing games");
        System.out.println("  play game    - join a game to play");
        System.out.println("  observe game - observe a game (white perspective)");
        System.out.println("  reset        - reset all games");
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
                String players;
                if (g.whiteUsername() == null && g.blackUsername() == null) {
                    players = "No players yet";
                } else {
                    String white = g.whiteUsername() != null ? g.whiteUsername() : "open";
                    String black = g.blackUsername() != null ? g.blackUsername() : "open";
                    players = "White: " + white + ", Black: " + black;
                }
                System.out.printf("%d. %s (%s)%n", i + 1, g.gameName(), players);
            }
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void playGame() {
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("No games to play. Use 'list games' first.");
            return;
        }
        try {
            System.out.print("Enter game number to join: ");
            int number = Integer.parseInt(scanner.nextLine().trim());
            if (number < 1 || number > lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData gameData = lastGames.get(number - 1);

            System.out.print("Enter color (white/black): ");
            String color = scanner.nextLine().trim().toLowerCase();
            if (!color.equals("white") && !color.equals("black")) {
                System.out.println("Invalid color. Choose 'white' or 'black'.");
                return;
            }

            facade.joinGame(gameData.gameID(), color);
            System.out.println("Joined game '" + gameData.gameName() + "' as " + color + "!");

            ChessBoardPrinter.drawBoard(gameData.game(), color);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input: please enter a number");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void observeGame() {
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("No games to observe. Use 'list games' first.");
            return;
        }
        try {
            System.out.print("Enter game number to observe: ");
            int number = Integer.parseInt(scanner.nextLine().trim());
            if (number < 1 || number > lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData gameData = lastGames.get(number - 1);
            System.out.println("Observing game '" + gameData.gameName() + "' (white perspective):");
            ChessBoardPrinter.drawBoard(gameData.game(), "white");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input: please enter a number");
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void printServerError(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            System.out.println("Error: " + msg);
        } else {
            System.out.println("Unknown error");
        }
    }
}