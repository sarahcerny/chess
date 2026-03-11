package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    // wipes literally everything bye bye data you were peak once
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    // gotta check the token first before showing any games
    public List<GameData> listGames(String playerToken) throws DataAccessException {

        // if we dont recognize the token they are NOT getting the list lol
        if (authDAO.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDAO.listGames();
    }

    // make a brand new chess game and store it in your bag
    public int createGame(String playerToken, String gameTitle) throws DataAccessException {
        if (authDAO.getAuth(playerToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // also need an actual name for the game
        if (gameTitle == null) {
            throw new DataAccessException("Error: bad request");
        }
        // 0 is placeholder id temporarily carrying the team
        GameData newGameBoard = new GameData(0, null, null, gameTitle, new ChessGame());
        return gameDAO.createGame(newGameBoard);
    }

    // player picking their side and jumping in go to war
    public void joinGame(String playerToken, String teamColor, int chessGameID) throws DataAccessException {
        AuthData playerSession = authDAO.getAuth(playerToken);
        //who are you
        if (playerSession == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        //are you real
        GameData currentGame = gameDAO.getGame(chessGameID);
        if (currentGame == null || teamColor == null) {
            throw new DataAccessException("Error: bad request");
        }
        //white spot taken finders keepers
        if (teamColor.equals("WHITE")) {
            if (currentGame.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            gameDAO.updateGame(new GameData(currentGame.gameID(), playerSession.username(), currentGame.blackUsername(), currentGame.gameName(), currentGame.game()));
        } else if (teamColor.equals("BLACK")) {
            //black spot taken finders keepers
            if (currentGame.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            //locking in black lets win this
            gameDAO.updateGame(new GameData(currentGame.gameID(), currentGame.whiteUsername(), playerSession.username(), currentGame.gameName(), currentGame.game()));
        } else {
            // teamColor has to be WHITE or BLACK mj song again
            throw new DataAccessException("Error: bad request");
        }
    }
}