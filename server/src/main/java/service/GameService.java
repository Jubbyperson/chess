package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import model.AuthData;
import model.GameData;
import service.requests.*;
import service.results.*;

import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws Exception {
        AuthData auth = dataAccess.getAuth(request.authToken());
        if (auth == null) {
            throw new Exception("unauthorized");
        }
        List<GameData> games = dataAccess.listGames();
        List<ListGamesResult.GameEntry> gameEntries = games.stream()
                .map(game -> new ListGamesResult.GameEntry(
                        game.gameID(),
                        game.whiteUser(),
                        game.blackUser(),
                        game.gameName()))
                .collect(Collectors.toList());
        return new ListGamesResult(gameEntries);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws Exception {
        AuthData auth = dataAccess.getAuth(request.authToken());
        if (auth == null) {
            throw new Exception("unauthorized");
        }
        int gameID = ((DataAccessMemory) dataAccess).getNextGameID();
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws Exception {
        AuthData auth = dataAccess.getAuth(request.authToken());
        if (auth == null) {
            throw new Exception("unauthorized");
        }

        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new Exception("bad request");
        }

        if ("WHITE".equals(request.playerColor()) && game.whiteUser() != null) {
            throw new Exception("already taken");
        }
        if ("BLACK".equals(request.playerColor()) && game.blackUser() != null) {
            throw new Exception("already taken");
        }

        GameData updatedGame;
        if ("WHITE".equals(request.playerColor())) {
            updatedGame = new GameData(game.gameID(), auth.username(), game.blackUser(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUser(), auth.username(), game.gameName(), game.game());
        }
        dataAccess.updateGame(updatedGame);
        return new JoinGameResult();
    }
}
