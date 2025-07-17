package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class DataAccessMemory implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public void clearAuth() throws DataAccessException {
        authTokens.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
}
