package client;

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

}
