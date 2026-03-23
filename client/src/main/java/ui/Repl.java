package ui;

import java.util.Scanner;

public class Repl {
    public Repl(String serverUrl) {
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equalsIgnoreCase("quit")) {
            System.out.print("[LOGGED_OUT] >>> ");
            line = scanner.nextLine().trim();
            System.out.println(eval(line));
        }
    }

    private String eval(String input) {
        return switch (input.toLowerCase()) {
            case "help" -> """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
            case "quit" -> "Goodbye!";
            default -> "Unknown command. Type help for options.";
        };
    }

}
