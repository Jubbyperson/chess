package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import service.requests.ListGamesRequest;
import service.results.ListGamesResult;

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
        List<ListGamesResult.GameEntry> gameEntries = games.stream().map(game -> new ListGamesResult.GameEntry(game.gameID(), game.whiteUser(), game.blackUser(), game.gameName())).collect(Collectors.toList());
        return new ListGamesResult(gameEntries);
    }

}
