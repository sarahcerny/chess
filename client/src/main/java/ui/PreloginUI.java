package ui;

import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

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
        System.out.println(SET_TEXT_COLOR_YELLOW + "Welcome to Chess! Type 'help' to get started.");
        while (true) {
            System.out.print(SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> ");
            String userAction = input.nextLine().trim();
            var inputParts = userAction.toLowerCase().split(" ");
            var holdInput = (inputParts.length > 0) ? inputParts[0] : "help";
            var args = Arrays.copyOfRange(inputParts, 1, inputParts.length);

            String result = switch (holdInput) {
                case "help" -> helpMenu();
                case "login" -> login(args);
                case "register" -> register(args);
                case "quit" -> {
                    System.out.println(SET_TEXT_COLOR_RED + "Goodbye!");
                    yield "quit";
                }
                default -> SET_TEXT_COLOR_RED + "Unknown command. Type 'help' to see available commands.";
            };
            System.out.println(result);
            if (result.equals("quit")) return;
        }
    }
    private String helpMenu() {
        return SET_TEXT_COLOR_BLUE + """
            Available commands:
              help                             - Show this help menu
              login <USERNAME> <PASSWORD>      - Log in to your account
              register <USERNAME> <PASSWORD> <EMAIL> - Create a new account
              quit                             - Exit the program
            """;
    }
    private String login(String[] args) {
        if (args.length < 2) {
            return SET_TEXT_COLOR_RED + "Missing arguments. Usage: login <USERNAME> <PASSWORD>";
        }
        if (args.length > 2) {
            return SET_TEXT_COLOR_RED + "Too many arguments. Usage: login <USERNAME> <PASSWORD>";
        }
        try {
            String username = args[0];
            String password = args[1];
            sessionToken = serverFacade.login(username, password);
            System.out.println(SET_TEXT_COLOR_GREEN + "Login successful! Welcome, " + username + "!");
            new PostloginUI(serverFacade, input).start();
            return SET_TEXT_COLOR_YELLOW + "Logged out. See you next time!";
        } catch (Exception e) {
            return handleError(e);
        }
    }
    private String register(String[] args) {
        if (args.length < 3) {
            return SET_TEXT_COLOR_RED + "Missing arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        if (args.length > 3) {
            return SET_TEXT_COLOR_RED + "Too many arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            String username = args[0];
            String password = args[1];
            String email = args[2];
            sessionToken = serverFacade.register(username, password, email);
            System.out.println(SET_TEXT_COLOR_GREEN + "Registered and logged in! Welcome, " + username + "!");
            new PostloginUI(serverFacade, input).start();
            return SET_TEXT_COLOR_YELLOW + "Logged out. See you next time!";
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private String handleError(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            return SET_TEXT_COLOR_RED + "Error: " + msg;
        }
        return SET_TEXT_COLOR_RED + "Something went wrong. Please try again.";
    }
}



