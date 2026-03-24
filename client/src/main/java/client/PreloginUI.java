package client;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreloginUI {
    private final ServerFacade serverFacade;
    private final String serverAddress;
    private String sessionToken;
    private final Scanner input;

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.serverAddress = serverFacade.getSessionToken();
        this.sessionToken = "";
        this.input = new Scanner(System.in);
    }

    public void run() {
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
            new PostloginUI(serverFacade, input).run();
            return SET_TEXT_COLOR_YELLOW + "Logged out. See you next time!";
        } catch (Exception e) {
            return handleError(e);
        }
    }






}


