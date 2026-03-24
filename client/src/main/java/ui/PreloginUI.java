package ui;

import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreloginUI {
    private final ServerFacade serverFacade;
    private String sessionToken;
    private final Scanner input;

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.sessionToken = "";
        this.input = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String userAction = input.nextLine().trim();
            var inputParts = userAction.toLowerCase().split(" ");
            var holdInput = (inputParts.length > 0) ? inputParts[0] : "help";
            var args = Arrays.copyOfRange(inputParts, 1, inputParts.length);

            String result = switch (holdInput) {
                case "help" -> helpMenu();
                case "login" -> login(args);
                case "register" -> register(args);
                case "quit" -> {
                    System.out.println("Goodbye!");
                    yield "quit";
                }
                default -> "Unknown command. Type 'help' to see available commands.";
            };
            System.out.println(result);
            if (result.equals("quit")) { return; }
        }
    }

    private String login(String[] args) {
        if (args.length < 2) {
            return "Missing arguments. Usage: login <USERNAME> <PASSWORD>";
        }
        if (args.length > 2) {
            return "Too many arguments. Usage: login <USERNAME> <PASSWORD>";
        }
        try {
            String username = args[0];
            String password = args[1];
            sessionToken = serverFacade.login(username, password);
            System.out.println("Login successful! Welcome, " + username + "!");
            new PostloginUI(serverFacade, input).start();
            return "Logged out. See you next time!";
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("unauthorized")) {
                return "Error: Login failed. Check your username and password.";
            }
            return "Error: " + e.getMessage();
        }
    }

    private String register(String[] args) {
        if (args.length < 3) {
            return "Missing arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        if (args.length > 3) {
            return "Too many arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            String username = args[0];
            String password = args[1];
            String email = args[2];
            sessionToken = serverFacade.register(username, password, email);
            System.out.println("Registered and logged in! Welcome, " + username + "!");
            new PostloginUI(serverFacade, input).start();
            return "Logged out. See you next time!";
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private String handleError(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            return "Error: " + msg;
        }
        return "Something went wrong. Please try again.";
    }

    private String helpMenu() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - exit the program
                - help - show this help message
                """;
    }
}