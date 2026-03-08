package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public List<GameData> listGames(String playerToken) throws DataAccessException {
        if (dataAccess.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return dataAccess.listGames();
    }
    public int createGame(String playerToken, String gameTitle) throws DataAccessException {
        if (dataAccess.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameTitle == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData newGameBoard = new GameData(0, null, null, gameTitle, new ChessGame());
        return dataAccess.createGame(newGameBoard);
    }

    public void joinGame(String playerToken, String teamColor, int chessGameID) throws DataAccessException {
        AuthData playerSession = dataAccess.getAuth(playerToken);
        if (playerSession == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData currentGame = dataAccess.getGame(chessGameID);
        if (currentGame == null || teamColor == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (teamColor.equals("WHITE")) {
            if (currentGame.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            dataAccess.updateGame(new GameData(currentGame.gameID(), playerSession.username(), currentGame.blackUsername(), currentGame.gameName(), currentGame.game()));
        } else if (teamColor.equals("BLACK")) {
            if (currentGame.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            dataAccess.updateGame(new GameData(currentGame.gameID(), currentGame.whiteUsername(), playerSession.username(), currentGame.gameName(), currentGame.game()));
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }
}