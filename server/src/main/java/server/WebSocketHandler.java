package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessages;
import websocket.messages.NotificationMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    // looks all sessions by session key to track
    private final Map<String, WsContext> playerSessions = new ConcurrentHashMap<>();
    // tracks which sessions belong to which game room which i need
    private final Map<Integer, Set<String>> roomSessions = new ConcurrentHashMap<>();

    private final Set<Integer> resignedGames = ConcurrentHashMap.newKeySet();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void onConnect(WsContext session) {
        playerSessions.put(session.sessionId(), session);
    }

    public void onClose(WsContext session) {
        playerSessions.remove(session.sessionId());
        roomSessions.values().forEach(set -> set.remove(session.sessionId()));
    }

    public void onMessage(WsMessageContext session) {
        try {
            UserGameCommand gameCommand = gson.fromJson(session.message(), UserGameCommand.class);
            switch (gameCommand.getCommandType()) {
                case CONNECT  -> handleConnect(session, gameCommand);
                case MAKE_MOVE -> handleMakeMove(session, gson.fromJson(session.message(), MoveCommands.class));
                case LEAVE    -> handleLeave(session, gameCommand);
                case RESIGN   -> handleResign(session, gameCommand);
            }
        } catch (Exception e) {
            notifyPlayer(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    //connect them all
    private void handleConnect(WsContext session, UserGameCommand gameCommand) throws DataAccessException {
        String playerToken = gameCommand.getAuthToken();
        int roomID = gameCommand.getGameID();

        AuthData auth = authDAO.getAuth(playerToken);
        if (auth == null) {
            notifyPlayer(session, new ErrorMessage("Error: invalid auth token"));
            return;
        }

        GameData activeGame = gameDAO.getGame(roomID);
        if (activeGame == null) {
            notifyPlayer(session, new ErrorMessage("Error: game not found"));
            return;
        }

        // add session to the room bc yk
        roomSessions.computeIfAbsent(roomID, k -> ConcurrentHashMap.newKeySet()).add(session.sessionId());

        notifyPlayer(session, new GameMessages(activeGame.game()));

        // notify everyone else in the room there here
        String playerName = auth.username();
        String role = getPlayerRole(playerName, activeGame);
        sendAll(roomID, session.sessionId(), new NotificationMessage(playerName + " joined as " + role));
    }

    // what do you do when you want to make a room
    private void handleMakeMove(WsContext session, MoveCommands gameCommand) throws DataAccessException {
        String playerToken = gameCommand.getAuthToken();
        int roomID = gameCommand.getGameID();
        ChessMove playerMove = gameCommand.getPlayerMove();

        AuthData auth = authDAO.getAuth(playerToken);
        if (auth == null) {
            notifyPlayer(session, new ErrorMessage("Error: invalid auth token"));
            return;
        }

        GameData activeGame = gameDAO.getGame(roomID);
        if (activeGame == null) {
            notifyPlayer(session, new ErrorMessage("Error: game not found"));
            return;
        }

        String playerName = auth.username();
        ChessGame chessGame = activeGame.game();

        // check game is not over yet bae
        if (resignedGames.contains(roomID) ||
                chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            notifyPlayer(session, new ErrorMessage("Error: game is already over"));
            return;
        }

// make sure player is actually in the game for reals

        // make sure player is actually in the game for reals
        boolean isWhite = playerName.equals(activeGame.whiteUsername());
        boolean isBlack = playerName.equals(activeGame.blackUsername());
        if (!isWhite && !isBlack) {
            notifyPlayer(session, new ErrorMessage("Error: observers cannot make moves"));
            return;
        }

        // hold up whoes turn is it
        ChessGame.TeamColor playerColor = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (chessGame.getTeamTurn() != playerColor) {
            notifyPlayer(session, new ErrorMessage("Error: it is not your turn"));
            return;
        }

        try {
            chessGame.makeMove(playerMove);
        } catch (chess.InvalidMoveException e) {
            notifyPlayer(session, new ErrorMessage("Error: invalid move"));
            return;
        }

        gameDAO.updateGame(new GameData(activeGame.gameID(), activeGame.whiteUsername(),
                activeGame.blackUsername(), activeGame.gameName(), chessGame));

        // send updated board spilling tea and lore to everyone
        GameMessages updatedGame = new GameMessages(chessGame);
        notifyPlayer(session, updatedGame);
        sendAll(roomID, session.sessionId(), updatedGame);

        String moveNote = playerName + " moved " + playerMove.getStartPosition() + " to " + playerMove.getEndPosition();
        sendAll(roomID, session.sessionId(), new NotificationMessage(moveNote));

        // check for checkmate or stalemate like old times
        ChessGame.TeamColor opponent = playerColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (chessGame.isInCheckmate(opponent)) {
            String msg = (opponent == ChessGame.TeamColor.WHITE ?
                    activeGame.whiteUsername() : activeGame.blackUsername()) + " is in checkmate!";
            notifyPlayer(session, new NotificationMessage(msg));
            sendAll(roomID, session.sessionId(), new NotificationMessage(msg));
        } else if (chessGame.isInStalemate(opponent)) {
            String msg = "Stalemate! The game is a draw.";
            notifyPlayer(session, new NotificationMessage(msg));
            sendAll(roomID, session.sessionId(), new NotificationMessage(msg));
        } else if (chessGame.isInCheck(opponent)) {
            String msg = (opponent == ChessGame.TeamColor.WHITE ?
                    activeGame.whiteUsername() : activeGame.blackUsername()) + " is in check!";
            notifyPlayer(session, new NotificationMessage(msg));
            sendAll(roomID, session.sessionId(), new NotificationMessage(msg));
        }
    }

    // now we need to handle leaving
    private void handleLeave(WsContext session, UserGameCommand gameCommand) throws DataAccessException {
        String playerToken = gameCommand.getAuthToken();
        int roomID = gameCommand.getGameID();

        AuthData auth = authDAO.getAuth(playerToken);
        if (auth == null) {
            notifyPlayer(session, new ErrorMessage("Error: invalid auth token"));
            return;
        }

        GameData activeGame = gameDAO.getGame(roomID);
        if (activeGame == null) {
            notifyPlayer(session, new ErrorMessage("Error: game not found"));
            return;
        }

        String playerName = auth.username();

        // remove player because they were once playing
        if (playerName.equals(activeGame.whiteUsername())) {
            gameDAO.updateGame(new GameData(activeGame.gameID(), null,
                    activeGame.blackUsername(), activeGame.gameName(), activeGame.game()));
        } else if (playerName.equals(activeGame.blackUsername())) {
            gameDAO.updateGame(new GameData(activeGame.gameID(), activeGame.whiteUsername(),
                    null, activeGame.gameName(), activeGame.game()));
        }

        Set<String> room = roomSessions.get(roomID);
        if (room != null) {
            room.remove(session.sessionId());
        }

        sendAll(roomID, session.sessionId(), new NotificationMessage(playerName + " left the game."));
    }

    // now we got to handle resign
    private void handleResign(WsContext session, UserGameCommand gameCommand) throws DataAccessException {
        String playerToken = gameCommand.getAuthToken();
        int roomID = gameCommand.getGameID();

        AuthData auth = authDAO.getAuth(playerToken);
        if (auth == null) {
            notifyPlayer(session, new ErrorMessage("Error: invalid auth token"));
            return;
        }

        GameData activeGame = gameDAO.getGame(roomID);
        if (activeGame == null) {
            notifyPlayer(session, new ErrorMessage("Error: game not found"));
            return;
        }

        String playerName = auth.username();

        // if you aint player you aint playing
        boolean isPlayer = playerName.equals(activeGame.whiteUsername()) ||
                playerName.equals(activeGame.blackUsername());
        if (!isPlayer) {
            notifyPlayer(session, new ErrorMessage("Error: observers cannot resign"));
            return;
        }

        // is it game over fr
        ChessGame chessGame = activeGame.game();
        if (resignedGames.contains(roomID) ||
                chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            notifyPlayer(session, new ErrorMessage("Error: game is already over"));
            return;
        }

        // a whole lot is going on here
        ChessGame.TeamColor resignColor = playerName.equals(activeGame.whiteUsername()) ?
                ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        chessGame.setTeamTurn(resignColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);

        // a whole lot is going on here
        resignedGames.add(roomID);
        gameDAO.updateGame(new GameData(activeGame.gameID(), activeGame.whiteUsername(),
                activeGame.blackUsername(), activeGame.gameName(), chessGame));

        String msg = playerName + " resigned. Game over!";
        notifyPlayer(session, new NotificationMessage(msg));
        sendAll(roomID, session.sessionId(), new NotificationMessage(msg));
    }

    public void clearResignedGames() {
        resignedGames.clear();
    }

    // now we need help
    private void sendAll(int roomID, String excludeSessionId, Object message) {
        Set<String> room = roomSessions.get(roomID);
        if (room == null) { return; }
        String json = gson.toJson(message);
        for (String sid : room) {
            if (!sid.equals(excludeSessionId)) {
                WsContext ctx = playerSessions.get(sid);
                if (ctx != null) {
                    ctx.send(json);
                }
            }
        }
    }

    private void notifyPlayer(WsContext session, Object message) {
        session.send(gson.toJson(message));
    }

    private String getPlayerRole(String playerName, GameData activeGame) {
        if (playerName.equals(activeGame.whiteUsername())) { return "WHITE"; }
        if (playerName.equals(activeGame.blackUsername())) { return "BLACK"; }
        return "an observer";
    }
}