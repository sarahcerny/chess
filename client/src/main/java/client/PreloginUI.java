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
}


