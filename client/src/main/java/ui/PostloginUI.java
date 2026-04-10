package ui;

import client.ServerFacade;
import model.GameData;


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
            if (line.isEmpty()) { continue;}

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();


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


    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  create <NAME>  - create a new game");
        System.out.println("  list           - list all games");
        System.out.println("  play <ID> <COLOR> - join and play");
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
        if (params.length < 2) {
            System.out.println("Missing inputs Usage: play <NUM> <COLOR>");
            return;
        }
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("Please run 'list' first.");
            return;
        }
        try {
            int index;
            try {
                index = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid format. Usage: play <NUM> <COLOR>");
                return;
            }
            String color = params[1].toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
                return;
            }
            if (index < 0 || index >= lastGames.size()) {
                System.out.println("Invalid game number type 'list' to see available games.");
                return;
            }
            model.GameData gameData = lastGames.get(index);
            facade.joinGame(gameData.gameID(), color);
            System.out.println("Joined successfully!");
            chess.ChessGame.TeamColor teamColor = color.equals("WHITE") ?
                    chess.ChessGame.TeamColor.WHITE : chess.ChessGame.TeamColor.BLACK;
            try {
                GameplayUI gameplay = new GameplayUI(facade.getServerUrl(), facade.getAuthToken(), gameData.gameID(), teamColor);
                gameplay.start();
            } catch (Exception ex) {
                System.out.println("Failed to connect to game: " + ex.getMessage());
            }
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void observeGame(String[] params) {
        if (params.length < 1) {
            System.out.println("Missing inputs: observe <NUM>");
            return;
        }
        if (lastGames == null || lastGames.isEmpty()) {
            System.out.println("Please run 'list' first.");
            return;
        }
        try {
            int index;
            try {
                index = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid game number.");
                return;
            }
            if (index < 0 || index >= lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }
            model.GameData gameData = lastGames.get(index);
            System.out.println("Observing " + gameData.gameName() + "!");
            GameplayUI gameplay = new GameplayUI(
                    facade.getServerUrl(),
                    facade.getAuthToken(),
                    gameData.gameID(),
                    chess.ChessGame.TeamColor.WHITE);
            gameplay.start();
        } catch (Exception e) {
            printServerError(e);
        }
    }

    private void printServerError(Exception e) {
        String msg = e.getMessage();
        if (msg == null) { System.out.println("Something went wrong. Please try again."); return; }
        if (msg.contains("403")) { System.out.println("That spot is already taken."); return; }
        if (msg.contains("401")) { System.out.println("Incorrect username or password."); return; }
        if (msg.contains("400")) { System.out.println("Please check your input."); return; }
        if (msg.contains("500")) { System.out.println("Something went wrong. Please try again."); return; }
        System.out.println("Something went wrong. Please try again.");
    }
}