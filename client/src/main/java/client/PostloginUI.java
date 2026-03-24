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

}